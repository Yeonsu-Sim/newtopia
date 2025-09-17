package io.ssafy.p.i13c203.gameserver.domain.gameresult.adapter;

import io.ssafy.p.i13c203.gameserver.domain.game.model.CardType;
import java.util.List;
import java.util.Optional;

/**
 * GameResult 조회용 읽기 포트.
 * GameService에서 히스토리 적재하는 형식을 그대로 읽어온다.
 */
public interface GameResultReader {

    /** 게임 소유권 확인 (memberId 기준) */
    boolean existsOwnedBy(Long gameId, Long memberId);

    /** 총 턴 수 */
    int countTurns(Long gameId);

    /** 최종 턴 수 */
    int getFinalTurnNumber(Long gameId);

    /** 최신 스냅샷(after) — entry.applied.after 우선, 없으면 entry.countryStats */
    Snapshot findLatestSnapshot(Long gameId);

    /** 구간(turnStart~turnEnd) 시계열 — asc 기준으로 리턴 */
    List<TurnValue> findTurnValuesInRange(Long gameId, int startTurn, int endTurn, boolean asc);

    /** 특정 턴 프레임(before/after/choice/card) */
    Optional<TurnFrame> findTurnFrame(Long gameId, int turnNumber);

    // ---- 최소 VO들 ----
    record Snapshot(int economy, int defense, int publicSentiment, int environment) {}
    record TurnValue(int turnNumber, int economy, int defense, int publicSentiment, int environment) {}

    record TurnFrame(
            int turnNumber,
            Snapshot before, Snapshot after,
            String choiceCode, String choiceLabel,
            Card card
    ) {
        public record Card(
                String title, String content, CardType type,
                String npcName, String npcImageUrl,
                List<Choice> choices,
                RelatedArticle related
        ) {
            public record Choice(String code, String label) {}
            public record RelatedArticle(String title, String url) {}
        }
    }
}
