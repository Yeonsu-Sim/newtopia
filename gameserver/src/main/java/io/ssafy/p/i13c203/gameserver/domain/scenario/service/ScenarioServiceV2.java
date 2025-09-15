package io.ssafy.p.i13c203.gameserver.domain.scenario.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.*;
import io.minio.messages.*;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.game.model.CountryStats;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import io.ssafy.p.i13c203.gameserver.domain.scenario.repository.ScenarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioServiceV2 implements ScenarioService{

    private final MinioClient minioClient;
    private final ScenarioRepository scenarioRepository;
    private final GenerateScenarioService generateScenarioService;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    @Value("${app.storage.minio.bucket-name}")
    private String bucketName;

    /**
     * MinIO에서 뉴스 데이터 검색 (JSON 파일)
     */
    public List<String> searchNewsFromMinio(String prefix) {
        List<String> newsFiles = new ArrayList<>();

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .recursive(true)
                    .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                String objectName = item.objectName();

                // 뉴스 JSON 파일만 필터링
                if (objectName.endsWith(".json")) {
                    newsFiles.add(objectName);
                }
            }

            log.info("Found {} news files with prefix: {}", newsFiles.size(), prefix);

        } catch (Exception e) {
            log.error("Failed to search news from MinIO", e);
        }

        return newsFiles;
    }

    /**
     * MinIO에서 뉴스 데이터 읽어오기
     */
    public JsonNode getNewsDataFromMinio(String objectKey) {
        try {
            InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .build()
            );

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            return objectMapper.readTree(content.toString());

        } catch (Exception e) {
            log.error("Failed to read news data from MinIO: {}", objectKey, e);
            return null;
        }
    }


    /**
     * S3 Select를 사용하여 MinIO에서 조건에 맞는 뉴스를 직접 쿼리
     */
    public List<JsonNode> queryNewsWithS3Select(Game game, String category, double minConfidence) {
        return queryNewsWithS3Select(game, category, minConfidence, null);
    }

    /**
     * S3 Select를 사용하여 MinIO에서 조건에 맞는 뉴스를 직접 쿼리 (연도 범위 지정)
     * @param game 게임 객체
     * @param category 뉴스 카테고리
     * @param minConfidence 최소 confidence 값
     * @param years 검색할 연도 목록 (null이면 전체 연도 2019-2024)
     */
    public List<JsonNode> queryNewsWithS3Select(Game game, String category, double minConfidence, List<Integer> years) {
        List<JsonNode> results = new ArrayList<>();

        try {
            CountryStats stats = game.getCountryStats();

            // 검색할 연도 목록 결정
            List<Integer> searchYears = (years != null && !years.isEmpty()) ?
                years : List.of(2019, 2020, 2021, 2022, 2023, 2024);

            log.debug("Searching news in years: {} for category: {}", searchYears, category);

            // 각 연도별로 파티션 검색
            for (Integer year : searchYears) {
                // 파티션 경로에서 파일 목록 가져오기
                String partitionPath = String.format("analyzed/year=%d/major=%s/", year, category);
                List<String> parquetFiles = searchNewsFromMinio(partitionPath);

                if (parquetFiles.isEmpty()) {
                    log.debug("No files found in partition: {}", partitionPath);
                    continue;
                }

                log.debug("Found {} files in partition: {}", parquetFiles.size(), partitionPath);

                // 각 파티션 파일에 대해 S3 Select 쿼리 실행 (연도당 최대 3개 파일)
                int filesPerYear = Math.min(parquetFiles.size(), 3);
                for (String file : parquetFiles.subList(0, filesPerYear)) {
                try {
                    // S3 Select가 지원되지 않으므로 기존 방식 사용 (성능 최적화)
                    JsonNode newsData = getNewsDataFromMinio(file);
                    if (newsData == null) continue;

                    // Java에서 필터링 (confidence 조건 체크)
                    JsonNode categories = newsData.path("categories");
                    JsonNode majorCategories = categories.path("major_categories");

                    if (majorCategories.isArray() && majorCategories.size() > 0) {
                        double confidence = majorCategories.get(0).path("confidence").asDouble();
                        if (confidence >= minConfidence) {
                            results.add(newsData);
                            log.debug("Added news with confidence: {} (>= {})", confidence, minConfidence);
                        } else {
                            log.debug("Skipped news with confidence: {} (< {})", confidence, minConfidence);
                        }
                    }

                } catch (Exception e) {
                    log.error("Failed to process file: {}", file, e);
                }

                    // 연도당 충분한 결과를 얻었으면 중단
                    if (results.size() >= 5) {
                        break;
                    }
                }

                // 전체적으로 충분한 결과를 얻었으면 연도 루프 중단
                if (results.size() >= 20) {
                    break;
                }
            }

            log.info("S3 Select found {} total results for category: {}, confidence >= {}, years: {}",
                    results.size(), category, minConfidence, searchYears);

        } catch (Exception e) {
            log.error("Failed to query news with S3 Select", e);
        }

        return results;
    }

    /**
     * 게임 상황을 분석하여 MinIO에서 적절한 뉴스를 선택 (S3 Select 활용)
     * 기본: 현재 연도만 검색
     */
    public JsonNode selectOptimalNewsFromMinio(Game game) {
        return selectOptimalNewsFromMinio(game, null);
    }

    /**
     * 게임 상황을 분석하여 MinIO에서 적절한 뉴스를 선택 (파티션 기반 최적화)
     * @param game 게임 객체
     * @param years 검색할 연도 목록 (null이면 현재 연도만)
     */
    public JsonNode selectOptimalNewsFromMinio(Game game, List<Integer> years) {
        try {
            CountryStats stats = game.getCountryStats();
            int currentTurn = game.getTurn();

            log.info("Selecting news for game {} - Economy: {}, Defense: {}, Environment: {}, PublicSentiment: {}",
                    game.getGameId(), stats.getEconomy(), stats.getDefense(),
                    stats.getEnvironment(), stats.getPublicSentiment());

            // 1. 게임 상황 기반 우선순위 카테고리 결정
            List<String> priorityCategories = getPriorityCategories(stats);

            // 2. S3 Select를 사용하여 각 카테고리에서 조건에 맞는 뉴스 쿼리
            List<JsonNode> allCandidates = new ArrayList<>();

            for (String category : priorityCategories) {
                // confidence 임계값은 해당 지표가 낮을수록 낮게 설정 (더 많은 뉴스 포함)
                double minConfidence = calculateMinConfidence(stats, category);

                List<JsonNode> categoryResults = queryNewsWithS3Select(game, category, minConfidence, years);
                allCandidates.addAll(categoryResults);

                log.debug("Category {} returned {} candidates with minConfidence {}",
                        category, categoryResults.size(), minConfidence);

                // 충분한 후보를 얻었으면 조기 종료
                if (allCandidates.size() >= 20) {
                    break;
                }
            }

            if (allCandidates.isEmpty()) {
                log.warn("No news candidates found with S3 Select");
                return null;
            }

            // 3. 후보들 중에서 최적의 뉴스 선택
            JsonNode bestNews = null;
            double bestScore = -1;

            for (JsonNode candidate : allCandidates) {
                double score = calculateNewsRelevanceScore(candidate, game);
                if (score > bestScore) {
                    bestScore = score;
                    bestNews = candidate;
                }
            }

            if (bestNews != null) {
                log.info("Selected best news with S3 Select - Title: {}, Score: {}",
                        bestNews.path("title").asText(), bestScore);
            }

            return bestNews;

        } catch (Exception e) {
            log.error("Failed to select optimal news from MinIO with S3 Select", e);
            return null;
        }
    }

    /**
     * 게임 상태에 따른 최소 confidence 임계값 계산
     */
    private double calculateMinConfidence(CountryStats stats, String category) {
        double statValue = switch (category) {
            case "economy" -> stats.getEconomy();
            case "defense" -> stats.getDefense();
            case "environment" -> stats.getEnvironment();
            case "publicSentiment" -> stats.getPublicSentiment();
            default -> 50.0;
        };

        // 지표가 낮을수록 더 낮은 confidence 허용 (더 많은 뉴스 포함)
        if (statValue < 30) return 0.2;      // 위험 상황: 낮은 품질도 허용
        else if (statValue < 50) return 0.3; // 보통 상황: 중간 품질 이상
        else return 0.4;                     // 안정 상황: 높은 품질만
    }

    /**
     * 게임 상황 기반 카테고리 우선순위 결정
     */
    private List<String> getPriorityCategories(CountryStats stats) {
        List<CategoryScore> scores = new ArrayList<>();

        // 지표가 낮을수록 높은 우선순위 (위험 상황 대응)
        scores.add(new CategoryScore("economy", 100 - stats.getEconomy()));
        scores.add(new CategoryScore("defense", 100 - stats.getDefense()));
        scores.add(new CategoryScore("environment", 100 - stats.getEnvironment()));
        scores.add(new CategoryScore("publicSentiment", 100 - stats.getPublicSentiment()));

        // 점수순으로 정렬 (높은 점수 = 높은 우선순위)
        scores.sort((a, b) -> Double.compare(b.score, a.score));

        List<String> priorityCategories = new ArrayList<>();
        for (CategoryScore cs : scores) {
            priorityCategories.add(cs.category);
        }

        log.debug("Category priority order: {}", priorityCategories);
        return priorityCategories;
    }

    /**
     * 카테고리 점수를 위한 헬퍼 클래스
     */
    private static class CategoryScore {
        String category;
        double score;

        CategoryScore(String category, double score) {
            this.category = category;
            this.score = score;
        }
    }

    /**
     * 뉴스와 게임 상태 간의 관련성 점수 계산
     */
    private double calculateNewsRelevanceScore(JsonNode newsData, Game game) {
        CountryStats stats = game.getCountryStats();
        double score = 0.0;

        try {
            // 뉴스 카테고리 정보 추출
            JsonNode categories = newsData.path("categories");
            JsonNode majorCategories = categories.path("major_categories");
            JsonNode debugSimilarities = categories.path("debug_similarities");

            // 1. 가장 낮은 지표에 해당하는 카테고리 우선 (위험 상황 대응)
            int minStat = Math.min(Math.min(stats.getEconomy(), stats.getDefense()),
                                  Math.min(stats.getEnvironment(), stats.getPublicSentiment()));

            String criticalCategory = "";
            if (minStat == stats.getEconomy()) criticalCategory = "economy";
            else if (minStat == stats.getDefense()) criticalCategory = "defense";
            else if (minStat == stats.getEnvironment()) criticalCategory = "environment";
            else if (minStat == stats.getPublicSentiment()) criticalCategory = "publicSentiment";

            // 2. 주요 카테고리 매칭 점수 (높은 가중치)
            if (majorCategories.isArray() && majorCategories.size() > 0) {
                JsonNode topCategory = majorCategories.get(0);
                String categoryName = topCategory.path("category").asText();
                double confidence = topCategory.path("confidence").asDouble();

                if (categoryName.equals(criticalCategory)) {
                    score += confidence * 3.0; // 위험 지표에 해당하는 카테고리면 3배 가중치
                } else {
                    score += confidence * 1.5; // 일반 주요 카테고리는 1.5배
                }
            }

            // 3. 디버그 유사도 점수 활용 (모든 카테고리 고려)
            if (debugSimilarities.has("economy")) {
                double economyRelevance = debugSimilarities.path("economy").asDouble();
                // 경제가 낮을수록 경제 뉴스의 점수를 높임
                score += economyRelevance * (100 - stats.getEconomy()) / 100.0;
            }

            if (debugSimilarities.has("defense")) {
                double defenseRelevance = debugSimilarities.path("defense").asDouble();
                score += defenseRelevance * (100 - stats.getDefense()) / 100.0;
            }

            if (debugSimilarities.has("environment")) {
                double environmentRelevance = debugSimilarities.path("environment").asDouble();
                score += environmentRelevance * (100 - stats.getEnvironment()) / 100.0;
            }

            if (debugSimilarities.has("publicSentiment")) {
                double publicSentimentRelevance = debugSimilarities.path("publicSentiment").asDouble();
                score += publicSentimentRelevance * (100 - stats.getPublicSentiment()) / 100.0;
            }

            // 4. 감정 점수 고려 (긍정/부정에 따른 가중치)
            JsonNode sentiment = newsData.path("sentiment");
            String sentimentLabel = sentiment.path("label").asText();
            double sentimentScore = sentiment.path("score").asDouble();

            if ("negative".equals(sentimentLabel)) {
                // 부정적 뉴스는 위기 상황에서 더 적절할 수 있음
                score += sentimentScore * 0.5;
            } else if ("positive".equals(sentimentLabel)) {
                // 긍정적 뉴스는 균형잡힌 상황에서 적절
                int avgStat = (stats.getEconomy() + stats.getDefense() +
                              stats.getEnvironment() + stats.getPublicSentiment()) / 4;
                if (avgStat > 40) { // 평균이 40 이상이면 긍정 뉴스 선호
                    score += sentimentScore * 0.3;
                }
            }

            // 5. 턴 수에 따른 조정 (게임 후반부에는 더 임팩트 있는 뉴스)
            if (game.getTurn() > 5) {
                score *= 1.2; // 후반부 뉴스 가중치 증가
            }

        } catch (Exception e) {
            log.error("Error calculating news relevance score", e);
            return 0.0;
        }

        return score;
    }

    @Override
    public Scenario firstScenario() {
        // 첫 시나리오는 기존 DB 데이터 사용 (뉴스 기반이 아님)
        try {
            List<Scenario> dbScenarios = scenarioRepository.findAll();
            if (!dbScenarios.isEmpty()) {
                return dbScenarios.get(random.nextInt(dbScenarios.size()));
            }
        } catch (Exception e) {
            log.error("Failed to get first scenario from database", e);
        }
        return null;
    }

    @Override
    public Scenario nextScenario(Game game, int nextTurn) {
        try {
            log.info("Generating scenario for game {} turn {}", game.getGameId(), nextTurn);

            // 1. 게임 상황을 분석하여 MinIO에서 최적의 뉴스 선택
            JsonNode selectedNews = selectOptimalNewsFromMinio(game);

            if (selectedNews == null) {
                log.warn("No suitable news found in MinIO, falling back to database scenarios");
                // 적절한 뉴스가 없으면 DB 시나리오 사용
                List<Scenario> dbScenarios = scenarioRepository.findAll();
                if (!dbScenarios.isEmpty()) {
                    return dbScenarios.get(random.nextInt(dbScenarios.size()));
                }
                return null;
            }

            log.info("Selected news for game {} - Title: {}, Category: {}, Sentiment: {}",
                    game.getGameId(),
                    selectedNews.path("title").asText(),
                    selectedNews.path("categories").path("major_categories").get(0).path("category").asText(),
                    selectedNews.path("sentiment").path("label").asText());

            // 2. GPT로 선택된 뉴스를 시나리오로 가공
            String generatedScenario = generateScenarioService.processNewsWithGPT(selectedNews, game);
            if (generatedScenario == null) {
                log.error("Failed to generate scenario with GPT for game: {}", game.getGameId());
                return null;
            }

            // 3. 가공된 시나리오로 Scenario 객체 생성
            return generateScenarioService.createScenarioFromGPTResult(generatedScenario, selectedNews);

        } catch (Exception e) {
            log.error("Failed to generate next scenario for game {} turn {}", game.getGameId(), nextTurn, e);
        }

        return null;
    }
}
