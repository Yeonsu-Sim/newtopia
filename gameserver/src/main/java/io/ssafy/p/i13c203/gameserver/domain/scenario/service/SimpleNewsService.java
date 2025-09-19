package io.ssafy.p.i13c203.gameserver.domain.scenario.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleNewsService {

    private final MinioClient minioClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String bucketName = "newtopia";
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final Random random = new Random();
    
    @PreDestroy
    public void cleanup() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }


    /**
     * 게임 조건에 맞는 뉴스 1개 조회
     */
    public JsonNode getNewsForGame(Game game, String category, String sentiment) {
        List<JsonNode> results = getNewsByCondition(category, sentiment, 1);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 무작위 뉴스 1개 조회
     */
    public JsonNode getRandomNews() {
        try {
            // MinIO에서 파일 목록 가져오기
//            List<String> fileNames = getFileList("sentiment/first/");


            int random1 = (int)(Math.random() * 80) + 1;
            List<String> fileNames = getFileList("sentiment/split/" + random1 + "/");


            if (fileNames.isEmpty()) {
                log.warn("뉴스 파일이 없습니다.");
                return null;
            }

            // 랜덤한 파일 선택
            String randomFileName = fileNames.get(random.nextInt(fileNames.size()));
            log.info("무작위 선택된 파일: {}", randomFileName);

            // 선택된 파일에서 무작위 뉴스 아이템 가져오기
            return getRandomNewsFromFile(randomFileName);

        } catch (Exception e) {
            log.error("무작위 뉴스 조회 중 오류 발생", e);
            return null;
        }
    }

    /**
     * 조건에 맞는 뉴스 조회 (병렬 처리)
     */
    private List<JsonNode> getNewsByCondition(String category, String sentiment, int limit) {
        try {
            // MinIO에서 파일 목록 가져오기

            // prifix 안에 또 150개 있음


            // sentiment/split/1~80

            int random = (int)(Math.random() * 80) + 1;
            List<String> fileNames = getFileList("sentiment/split/" + random);

            log.info("file name isEmpty : {}", fileNames.isEmpty());
            
            // 병렬로 파일들을 처리하여 조건에 맞는 뉴스 찾기
            List<CompletableFuture<List<JsonNode>>> futures = fileNames.stream()
                    .map(fileName -> CompletableFuture.supplyAsync(() -> 
                        processFile(fileName, category, sentiment, limit), executor))
                    .toList();

            // 모든 작업 완료 대기 및 결과 수집
            List<JsonNode> allResults = new ArrayList<>();
            for (CompletableFuture<List<JsonNode>> future : futures) {
                List<JsonNode> results = future.get();
                allResults.addAll(results);
                if (allResults.size() >= limit) {
                    break;
                }
            }

            return allResults.stream().limit(limit).toList();

        } catch (Exception e) {
            log.error("뉴스 조회 중 오류 발생", e);
            return List.of();
        }
    }

    /**
     * MinIO에서 파일 목록 가져오기
     */
    private List<String> getFileList(String prefix) {
        List<String> fileNames = new ArrayList<>();
        try {
            log.info("MinIO 파일 목록 조회 시작 - bucket: {}, prefix: {}", bucketName, prefix);
            
            ListObjectsArgs listArgs = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .build();

            Iterable<Result<Item>> results = minioClient.listObjects(listArgs);
            int totalCount = 0;
            int jsonCount = 0;
            
            for (Result<Item> result : results) {
                String objectName = result.get().objectName();
                totalCount++;
                log.debug("발견된 객체: {}", objectName);
                
                if (objectName.endsWith(".json")) {
                    fileNames.add(objectName);
                    jsonCount++;
                    log.debug("JSON 파일 추가: {}", objectName);
                }
            }
            
            log.info("파일 목록 조회 완료 - 전체 객체 수: {}, JSON 파일 수: {}", totalCount, jsonCount);
            
            if (jsonCount > 0) {
                log.info("발견된 JSON 파일들:");
                for (int i = 0; i < Math.min(fileNames.size(), 5); i++) {
                    log.info("  {}: {}", i+1, fileNames.get(i));
                }
                if (fileNames.size() > 5) {
                    log.info("  ... 총 {}개 파일", fileNames.size());
                }
            }
            
        } catch (Exception e) {
            log.error("파일 목록 조회 중 오류 발생 - bucket: {}, prefix: {}", bucketName, prefix, e);
        }
        return fileNames;
    }

    /**
     * 게임 상태에 따라 적절한 뉴스 조회
     */
    public JsonNode getNewsForGameState(Game game) {
        try {
            // 게임 상태 분석
            String targetCategory = analyzeGameStateForCategory(game);
            String targetSentiment = analyzeGameStateForSentiment(game);
            
            log.info("게임 {}턴 - 추천 카테고리: {}, 감정: {}", game.getTurn(), targetCategory, targetSentiment);
            
            // 조건에 맞는 뉴스 조회
            JsonNode selectedNews = getNewsForGame(game, targetCategory, targetSentiment);
            
            if (selectedNews != null) {
                log.info("선택된 뉴스: {}", selectedNews.get("title").asText());
            } else {
                log.warn("조건에 맞는 뉴스를 찾을 수 없습니다. 기본 뉴스를 반환합니다.");
                // 조건을 완화해서 다시 시도
                selectedNews = getNewsForGame(game, null, targetSentiment);
            }
            
            return selectedNews;
            
        } catch (Exception e) {
            log.error("게임 상태 기반 뉴스 조회 중 오류 발생", e);
            return null;
        }
    }

    /**
     * 게임 상태를 분석하여 적절한 카테고리 결정
     */
    private String analyzeGameStateForCategory(Game game) {
        // countryStats에서 가장 낮은 점수의 카테고리를 우선적으로 선택
        // (문제가 있는 영역에 대한 뉴스를 제공)
        
        if (game.getCountryStats() == null) {
            return "economy"; // 기본값
        }
        
        // 예시: countryStats가 Map<String, Integer> 형태라고 가정
        // 실제 구조에 맞춰 조정 필요
        int economyScore = getStatScore(game, "economy");
        int defenseScore = getStatScore(game, "defense");
        int environmentScore = getStatScore(game, "environment");
        int publicSentimentScore = getStatScore(game, "publicSentiment");
        
        // 가장 낮은 점수의 카테고리를 반환 (문제 영역 우선 처리)
        int minScore = Math.min(Math.min(economyScore, defenseScore), 
                               Math.min(environmentScore, publicSentimentScore));
        
        if (economyScore == minScore) return "economy";
        if (defenseScore == minScore) return "defense";
        if (environmentScore == minScore) return "environment";
        if (publicSentimentScore == minScore) return "publicSentiment";
        
        return "economy"; // 기본값
    }

    /**
     * 게임 상태를 분석하여 적절한 감정 결정
     */
    private String analyzeGameStateForSentiment(Game game) {
        // 턴수나 전체적인 상황을 보고 감정 결정
        int currentTurn = game.getTurn();
        
        // 초반에는 중성적인 뉴스, 후반으로 갈수록 부정적인 뉴스 증가
        if (currentTurn <= 3) {
            return Math.random() > 0.3 ? "positive" : "negative";
        } else if (currentTurn <= 7) {
            return Math.random() > 0.5 ? "positive" : "negative";
        } else {
            return Math.random() > 0.7 ? "positive" : "negative"; // 후반엔 주로 부정적
        }
    }

    /**
     * 게임에서 특정 카테고리 점수 조회 (실제 Game 구조에 맞춰 수정 필요)
     */
    private int getStatScore(Game game, String category) {
        // TODO: 실제 Game 엔티티 구조에 맞춰 구현
        // 예시 코드
        try {
            // countryStats가 어떤 구조인지에 따라 수정 필요
            return 50; // 기본값
        } catch (Exception e) {
            log.warn("카테고리 {} 점수 조회 실패", category);
            return 50;
        }
    }

    /**
     * 개별 파일 처리
     */
    private List<JsonNode> processFile(String fileName, String category, String sentiment, int maxResults) {
        List<JsonNode> results = new ArrayList<>();
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build();

            InputStream stream = minioClient.getObject(getObjectArgs);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String line;
            while ((line = reader.readLine()) != null && results.size() < maxResults) {
                if (!line.trim().isEmpty()) {
                    JsonNode newsItem = objectMapper.readTree(line);
                    
                    if (matchesCondition(newsItem, category, sentiment)) {
                        results.add(newsItem);
                    }
                }
            }
            reader.close();

        } catch (Exception e) {
            log.error("파일 처리 중 오류 발생: {}", fileName, e);
        }
        return results;
    }

    /**
     * 뉴스가 조건에 맞는지 확인
     */
    private boolean matchesCondition(JsonNode newsItem, String category, String sentiment) {
        try {
            // 감정 체크
            if (sentiment != null) {
                JsonNode sentimentNode = newsItem.get("sentiment");
                if (sentimentNode == null || !sentiment.equals(sentimentNode.get("label").asText())) {
                    return false;
                }
            }

            // 카테고리 체크
            if (category != null) {
                JsonNode categoriesNode = newsItem.get("categories");
                if (categoriesNode != null) {
                    JsonNode majorCategories = categoriesNode.get("major_categories");
                    if (majorCategories != null && majorCategories.isArray() && majorCategories.size() > 0) {
                        String newsCategory = majorCategories.get(0).get("category").asText();
                        if (!category.equals(newsCategory)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 파일에서 무작위 뉴스 아이템 가져오기
     */
    private JsonNode getRandomNewsFromFile(String fileName) {
        List<JsonNode> allNewsItems = new ArrayList<>();

        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build();

            InputStream stream = minioClient.getObject(getObjectArgs);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        JsonNode newsItem = objectMapper.readTree(line);
                        allNewsItems.add(newsItem);
                    } catch (Exception e) {
                        log.debug("JSON 파싱 실패한 라인 스킵: {}", line.substring(0, Math.min(line.length(), 50)));
                    }
                }
            }
            reader.close();

            if (allNewsItems.isEmpty()) {
                log.warn("파일 {}에서 유효한 뉴스를 찾을 수 없습니다.", fileName);
                return null;
            }

            // 무작위 뉴스 아이템 선택
            JsonNode randomNews = allNewsItems.get(random.nextInt(allNewsItems.size()));
            log.info("파일 {}에서 {}개 중 무작위 뉴스 선택: {}",
                fileName, allNewsItems.size(), randomNews.get("title").asText());

            return randomNews;

        } catch (Exception e) {
            log.error("파일 {}에서 무작위 뉴스 조회 중 오류 발생", fileName, e);
            return null;
        }
    }
}