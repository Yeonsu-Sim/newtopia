package io.ssafy.p.i13c203.gameserver.domain.gameresult.service;

import io.ssafy.p.i13c203.gameserver.common.dto.request.SortDirection;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.CountryStatsDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.doc.ContextDoc;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.mapper.GameResultContextDtoMapper;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.mapper.GameResultSummaryDtoMapper;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.request.DetailQuery;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.request.Metric;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.request.Part;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.request.ReportQuery;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.response.*;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.entity.GameResult;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.reader.GameResultReader;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.repository.GameResultRepository;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.repository.GameResultSummaryRepository;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;
import io.ssafy.p.i13c203.gameserver.global.exception.NotFoundException;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 컨텍스트/요약은 저장 테이블에서 읽고, 그래프는 히스토리 기반으로 즉시 생성한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(Transactional.TxType.SUPPORTS)
public class GameResultServiceImpl implements GameResultService {

    private static final List<Metric> ALL_METRICS =
            List.of(Metric.ECONOMY, Metric.DEFENSE, Metric.PUBLIC_SENTIMENT, Metric.ENVIRONMENT);
    private static final Set<Part> ALL_PARTS =
            EnumSet.of(Part.CONTEXT, Part.SUMMARY, Part.GRAPH);

    private final GameResultReader reader;                       // 권한/히스토리/턴 조회 포트
    private final GameResultRepository gameResultRepository;     // context
    private final GameResultSummaryRepository summaryRepository; // summary
    private final GameResultSummaryOrchestrator orchestrator;

    private final GameResultContextDtoMapper contextMapper;
    private final GameResultSummaryDtoMapper summaryDtoMapper;


    @Override
    @Transactional
    public void createOnEnding(Long memberId, Game game) {
        Long gameId = game.getId();
        ensureOwned(gameId, memberId);

        // 이미 존재하면 생성 스킵 (불변 컨텍스트)
        var existing = gameResultRepository.findByGameId(gameId).orElse(null);
        if (existing != null) return;

        // 히스토리 기반 finalTurnNumber/최종 after 스냅샷
        int finalTurnNumber = reader.getFinalTurnNumber(gameId);
        var latest = reader.findLatestSnapshot(gameId); // entry.applied.after (fallback은 Reader 구현에서)
        CountryStatsDoc finalStats = new CountryStatsDoc(
                latest.economy(), latest.defense(), latest.publicSentiment(), latest.environment()
        );

        var context = new ContextDoc(
                game.getCountryName(),
                finalTurnNumber,
                Instant.now().toString(),
                finalStats
        );
        var gr = gameResultRepository.save(GameResult.builder()
                .gameId(gameId)
                .context(context)
                .build());

        // ai 요약 비동기 생성 호출을 트랜잭션 커밋 이후로 미룬다
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                orchestrator.ensureJob(gameId, null);
            }
        });
    }

    @Override
    public GameResultReportResponse getReport(Long memberId, Long gameId, ReportQuery query) {
        ensureOwned(gameId, memberId);

        var parts   = resolveParts(query.parts());
        var metrics = resolveMetrics(query.metrics());

        // --- context: game_result.context (불변) ---
        ContextDto contextDto = null;
        GameResult gr = null;
        if (parts.contains(Part.CONTEXT) || parts.contains(Part.GRAPH) || parts.contains(Part.SUMMARY)) {
            gr = gameResultRepository.findByGameId(gameId)
                    .orElseThrow(() -> notFound("결과가 존재하지 않습니다. (game_result)"));
        }
        if (parts.contains(Part.CONTEXT)) {
            var ctx = Objects.requireNonNull(gr).getContext();
            contextDto = contextMapper.toContext(gr);
        }


        // --- summary: game_result_summary.summary ---
        SummaryDto summaryDto = null;
        if (parts.contains(Part.SUMMARY)) {
            var grId = Objects.requireNonNull(gr).getId();
            var s = summaryRepository.findByGameResultId(grId).orElse(null);

            // 요약 상태 읽기만 수행
            if (s == null) {
                orchestrator.ensureJob(gameId, null);
                s = summaryRepository.findByGameResultId(grId).orElseThrow(
                        () -> new NotFoundException(ErrorCode.NOT_FOUND, "요약을 찾을 수 없습니다. gameResultId="+grId)
                );
            }
            summaryDto = summaryDtoMapper.toSummary(s.getSummary());

            // TODO: 미사용 주석 수거 보류
//            if (s != null && s.getSummary() != null && s.getSummary().status() == SummaryStatus.READY) {
//                // READY 그대로 반환
//                summaryDto = summaryDtoMapper.toReadySummary(s.getSummary());
//            } else {
//                // 없거나 PENDING/ERROR
//                summaryDto = summaryDtoMapper.toSummary(orchestrator.ensureJob(gameId, null));
//            }
        }


        // --- graph: game_history on-the-fly ---
        GraphDto graphDto = null;
        if (parts.contains(Part.GRAPH)) {
            graphDto = buildGraph(
                    gameId,
                    Objects.requireNonNull(gr).getContext().finalTurnNumber()+1,
                    query.page(), query.size(), query.sort(),
                    metrics
            );
        }

        return new GameResultReportResponse(contextDto, summaryDto, graphDto);
    }

    @Override
    public GameResultDetailResponse getTurnDetail(Long memberId, Long gameId, Integer turnNumber, DetailQuery query) {
        ensureOwned(gameId, memberId);

        var gr = gameResultRepository.findByGameId(gameId)
                .orElseThrow(() -> notFound("결과가 존재하지 않습니다. (game_result)"));
        int finalTurnNumber = gr.getContext().finalTurnNumber();
        if (turnNumber == null || turnNumber < 0 || turnNumber > Math.max(0, finalTurnNumber)) {
            throw notFound("존재하지 않는 턴입니다.");
        }

        var metrics = resolveMetrics(query.metrics());
        var frame = reader.findTurnFrame(gameId, turnNumber)
                .orElseThrow(() -> notFound("해당 턴 정보를 찾을 수 없습니다."));

        var beforeStats = toStats(frame.before());
        var afterStats  = toStats(frame.after());
        var deltaStats  = new GameResultDetailResponse.CountryStats(
                afterStats.economy() - beforeStats.economy(),
                afterStats.defense() - beforeStats.defense(),
                afterStats.publicSentiment() - beforeStats.publicSentiment(),
                afterStats.environment() - beforeStats.environment()
        );

        var context = new GameResultDetailResponse.Context(
                turnNumber, finalTurnNumber,
                metrics.stream().map(this::metricKey).toList()
        );
        var applied = new GameResultDetailResponse.Applied(
                turnNumber, frame.choiceCode(), frame.choiceLabel(),
                new GameResultDetailResponse.CountryStatsGroup(beforeStats, afterStats, deltaStats)
        );
        var card = toCard(frame.card());

        return new GameResultDetailResponse(context, applied, card);
    }

    // ===== Helpers =====

    private void ensureOwned(Long gameId, Long memberId) {
        if (!reader.existsOwnedBy(gameId, memberId)) {
            throw forbidden("해당 리소스를 조회할 수 없습니다.");
        }
    }

    private Set<Part> resolveParts(Set<Part> parts) {
        if (parts == null || parts.isEmpty()) return EnumSet.copyOf(ALL_PARTS);
        return EnumSet.copyOf(parts);
    }

    private List<Metric> resolveMetrics(List<Metric> metrics) {
        if (metrics == null || metrics.isEmpty()) return ALL_METRICS;
        return metrics.stream().distinct().collect(Collectors.toList());
    }

    private GraphDto buildGraph(
            Long gameId, int totalCount,             // finalTurnNumber + 1 이 들어옴
            Integer page, Integer size, SortDirection sort,
            List<Metric> metrics
                                                     ) {
        int perPage = (size == null || size < 1) ? 12 : size;
        int pages   = Math.max(1, (int) Math.ceil(totalCount / (double) perPage));
        int n       = (page == null || page < 1) ? 1 : Math.min(page, pages);

        // 0-based inclusive range 계산
        int start, end;
        if (sort == SortDirection.ASC) {
            start = (n - 1) * perPage;
            end   = Math.min(start + perPage - 1, totalCount - 1); // ← 반드시 totalCount-1
        } else { // DESC
            end   = (totalCount - 1) - (n - 1) * perPage;
            start = Math.max(0, end - perPage + 1);
        }

        // 항상 ASC로 뽑고, DESC면 뒤집기
        var rowsAsc = reader.findTurnValuesInRange(gameId, start, end, true);
        List<GameResultReader.TurnValue> rowsOrdered =
                (sort == SortDirection.ASC) ? rowsAsc : new ArrayList<>(rowsAsc);
        if (sort == SortDirection.DESC) Collections.reverse(rowsOrdered);

        var series = new ArrayList<GraphDto.Series>();
        for (Metric m : metrics) {
            var points = rowsOrdered.stream()
                    .map(tv -> new GraphDto.Point(tv.turnNumber(), valueOf(tv, m)))
                    .toList();
            series.add(new GraphDto.Series(metricKey(m), points));
        }

        var pageDto = new GraphDto.Page(
                n, perPage, sort.name().toLowerCase(),
                totalCount, pages,
                n < pages, n > 1,
                n < pages ? n + 1 : null,
                n > 1 ? n - 1 : null
        );

        return new GraphDto(
                metrics.stream().map(this::metricKey).toList(),
                pageDto,
                series
        );
    }


    private GameResultDetailResponse.CountryStats toStats(GameResultReader.Snapshot s) {
        if (s == null) return new GameResultDetailResponse.CountryStats(0,0,0,0);
        return new GameResultDetailResponse.CountryStats(s.economy(), s.defense(), s.publicSentiment(), s.environment());
    }

    private String metricKey(Metric m) {
        return switch (m) {
            case ECONOMY -> "economy";
            case DEFENSE -> "defense";
            case ENVIRONMENT -> "environment";
            case PUBLIC_SENTIMENT -> "publicSentiment";
        };
    }

    private int valueOf(GameResultReader.TurnValue tv, Metric m) {
        return switch (m) {
            case ECONOMY -> tv.economy();
            case DEFENSE -> tv.defense();
            case PUBLIC_SENTIMENT -> tv.publicSentiment();
            case ENVIRONMENT -> tv.environment();
        };
    }

    private GameResultDetailResponse.Card toCard(GameResultReader.TurnFrame.Card c) {
        if (c == null) return null;
        var npc = new GameResultDetailResponse.Npc(c.npcName(), c.npcImageUrl());
        var choices = c.choices() == null ? List.<GameResultDetailResponse.Choice>of() :
                c.choices().stream().map(
                        ch -> new GameResultDetailResponse.Choice(ch.code(),
                                ch.label(),
                                GameResultDetailResponse.PressRelease.from(ch.pressRelease()),
                                ch.comments()
                        ))
                        .toList();
        var related = c.related() == null ? null :
                new GameResultDetailResponse.RelatedArticle(c.related().title(), c.related().url(), c.related().content());
        return new GameResultDetailResponse.Card(c.title(), c.content(), c.type(), npc, choices, related);
    }

    private RuntimeException forbidden(String msg) { return new NotFoundException(ErrorCode.NOT_FOUND, msg); }
    private RuntimeException notFound(String msg) { return new NotFoundException(ErrorCode.NOT_FOUND, msg); }
}
