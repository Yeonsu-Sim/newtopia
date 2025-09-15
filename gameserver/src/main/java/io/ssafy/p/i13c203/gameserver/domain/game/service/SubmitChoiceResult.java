package io.ssafy.p.i13c203.gameserver.domain.game.service;

import io.ssafy.p.i13c203.gameserver.domain.ending.doc.EndingDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.CardDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.CountryStatsDoc;

public record SubmitChoiceResult(
        Applied applied,        // 적용 "이전" 스냅샷 + 선택코드
        NextTurn nextTurn,      // 적용 "이후" 스냅샷 + 다음 카드(null = 엔딩)
        boolean gameOver,       // 종료 여부
        EndingDoc ending        // 종료 시 엔딩 정보 (아니면 null)
) {
    public static SubmitChoiceResult progress(
            int finishedTurn, String choosedCode,
            CountryStatsDoc beforeStats,       // 적용 이전
            int nextTurnNumber, CountryStatsDoc afterStats, CardDoc nextCard
                                             ) {
        return new SubmitChoiceResult(
                new Applied(finishedTurn, choosedCode, beforeStats),
                new NextTurn(nextTurnNumber, afterStats, nextCard),
                false,
                null
        );
    }

    public static SubmitChoiceResult ended(
            int finishedTurn, String choosedCode,
            CountryStatsDoc beforeStats,       // 적용 이전
            CountryStatsDoc afterStats,        // 적용 이후
            EndingDoc ending
                                          ) {
        // 엔딩이면 다음 카드는 없음, 턴은 고정
        return new SubmitChoiceResult(
                new Applied(finishedTurn, choosedCode, beforeStats),
                new NextTurn(finishedTurn, afterStats, null),
                true,
                ending
        );
    }

    public record Applied(int turn, String choosedCode, CountryStatsDoc countryStats) {}
    public record NextTurn(int turn, CountryStatsDoc countryStats, CardDoc card) {}
}
