package io.ssafy.p.i13c203.gameserver.domain.gameresult.adapter;

import io.ssafy.p.i13c203.gameserver.domain.game.doc.CardDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.CountryStatsDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.HistoryEntryDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.GameHistory;
import io.ssafy.p.i13c203.gameserver.domain.game.repository.GameHistoryRepository;
import io.ssafy.p.i13c203.gameserver.domain.game.repository.GameRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * GameResultReader의 기본 JPA 구현.
 * - 소유권 확인: GameRepository.existsByIdAndMemberId
 * - 시계열/드릴다운: GameHistory.entry(HistoryEntryDoc) 기반
 */
@Component
@RequiredArgsConstructor
public class GameResultReaderImpl implements GameResultReader {

    private final GameRepository gameRepository;
    private final GameHistoryRepository historyRepository;

    // ========== 권한/존재 ==========

    @Override
    public boolean existsOwnedBy(Long gameId, Long memberId) {
        return gameRepository.existsByIdAndMemberId(gameId, memberId);
    }

    // ========== 카운트/최신 스냅샷 ==========

    @Override
    public int countTurns(Long gameId) {
        return historyRepository.countByGameId(gameId);
    }

    @Override
    public int getFinalTurnNumber(Long gameId) {
        return historyRepository.findTopByGameIdOrderByTurnDesc(gameId)
                .orElseThrow(() -> new NoSuchElementException("히스토리가 비어 있습니다."))
                .getTurn();
    }

    @Override
    public Snapshot findLatestSnapshot(Long gameId) {
        var latestOpt = historyRepository.findTopByGameIdOrderByTurnDesc(gameId);
        var latest = latestOpt.orElseThrow(() -> new java.util.NoSuchElementException("히스토리가 비어 있습니다."));
        var entry = latest.getEntry(); // HistoryEntryDoc
        var after = preferAppliedAfter(entry);
        return new Snapshot(after.economy(), after.defense(), after.publicSentiment(), after.environment());
    }

    // ========== 구간 시계열 ==========

    @Override
    public List<TurnValue> findTurnValuesInRange(Long gameId, int startTurn, int endTurn, boolean ascIgnored) {
        var rowsAsc = historyRepository
                .findAllByGameIdAndTurnBetweenOrderByTurnAsc(gameId, startTurn, endTurn);
        var values = new ArrayList<TurnValue>(rowsAsc.size());
        for (GameHistory gh : rowsAsc) {
            var after = preferAppliedAfter(gh.getEntry());
            values.add(new TurnValue(gh.getTurn(), after.economy(), after.defense(),
                    after.publicSentiment(), after.environment()));
        }
        return values; // 항상 ASC
    }


    // ========== 드릴다운 프레임 ==========

    @Override
    public Optional<TurnFrame> findTurnFrame(Long gameId, int turnNumber) {
        var curOpt = historyRepository.findByGameIdAndTurn(gameId, turnNumber);
        if (curOpt.isEmpty()) return Optional.empty();

        var cur = curOpt.get();
        var curEntry = cur.getEntry();

        // after: applied.after 우선, 없으면 countryStats
        var afterDoc = preferAppliedAfter(curEntry);

        // before: applied.before 우선, 없으면 (이전 턴 after)로 복원, 그것도 없으면 0으로 채움
        CountryStatsDoc beforeDoc = null;
        if (curEntry.applied() != null && curEntry.applied().before() != null) {
            beforeDoc = curEntry.applied().before();
        } else {
            var prevOpt = historyRepository.findByGameIdAndTurn(gameId, turnNumber - 1);
            if (prevOpt.isPresent()) {
                beforeDoc = preferAppliedAfter(prevOpt.get().getEntry());
            } else {
                beforeDoc = new CountryStatsDoc(0, 0, 0, 0);
            }
        }

        var beforeSnap = new Snapshot(beforeDoc.economy(), beforeDoc.defense(), beforeDoc.publicSentiment(), beforeDoc.environment());
        var afterSnap  = new Snapshot(afterDoc.economy(),  afterDoc.defense(),  afterDoc.publicSentiment(),  afterDoc.environment());

        var appliedChoiceCode  = nullToEmpty(curEntry.choosedCode());
        var appliedChoiceLabel = nullToEmpty(curEntry.choosedLabel());

        // 카드 매핑
        CardDoc cardDoc = curEntry.card(); // CardDoc or null
        TurnFrame.Card card = null;
        if (cardDoc != null) {
            var choices = new ArrayList<TurnFrame.Card.Choice>();
            if (cardDoc.choices() != null) {
                for (var ch : cardDoc.choices().values()) {
                    choices.add(new TurnFrame.Card.Choice(ch.code(), ch.label(), ch.pressRelease(), ch.comments()));
                }
            }
            TurnFrame.Card.RelatedArticle related = null;
            if (cardDoc.relatedArticle() != null) {
                related = new TurnFrame.Card.RelatedArticle(cardDoc.relatedArticle().title(), cardDoc.relatedArticle().url(), cardDoc.relatedArticle().content());
            }
            card = new TurnFrame.Card(
                    cardDoc.title(),
                    cardDoc.content(),
                    cardDoc.type(),
                    cardDoc.npc() != null ? cardDoc.npc().name() : null,
                    cardDoc.npc() != null ? cardDoc.npc().imageUrl() : null,
                    choices,
                    related
            );
        }

        return Optional.of(new TurnFrame(turnNumber, beforeSnap, afterSnap, appliedChoiceCode, appliedChoiceLabel, card));
    }

    // ========== 내부 헬퍼 ==========

    private static CountryStatsDoc preferAppliedAfter(HistoryEntryDoc entry) {
        if (entry == null) return new CountryStatsDoc(0,0,0,0);
        if (entry.applied() != null && entry.applied().after() != null) {
            return entry.applied().after();
        }
        // 하위호환: countryStats는 "after" 의미로 저장되어 왔음
        if (entry.countryStats() != null) return entry.countryStats();
        return new CountryStatsDoc(0,0,0,0);
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
