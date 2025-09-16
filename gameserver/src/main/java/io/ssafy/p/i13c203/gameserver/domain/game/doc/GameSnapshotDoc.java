package io.ssafy.p.i13c203.gameserver.domain.game.doc;

import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.game.model.CountryStats;
import io.ssafy.p.i13c203.gameserver.domain.game.model.ChoiceWeights;
import java.time.Instant;
import java.time.LocalDateTime;

public record GameSnapshotDoc(
        Long gameId,               // 게임 ID
        Long memberId,             // 회원 ID
        String countryName,        // 국가명
        CountryStats countryStats, // 국가 스탯
        ChoiceWeights choiceWeights, // 선택 가중치
        int turn,                  // 턴 번호
        String choosedCode,        // 선택한 코드
        boolean active,            // 활성화 상태
        LocalDateTime endedAt,           // 종료 시간 (있으면 종료된 게임)
        String endingCode          // 엔딩 코드
) {

    // Game 객체에서 Snapshot을 생성
    public static GameSnapshotDoc from(Game game) {
        return new GameSnapshotDoc(
                game.getGameId(),
                game.getMemberId(),
                game.getCountryName(),
                game.getCountryStats(),
                game.getChoiceWeights(),
                game.getTurn(),
                game.getChoosedCode(),
                game.isActive(),
                game.getEndedAt(),
                game.getEndingCode()
        );
    }
}
