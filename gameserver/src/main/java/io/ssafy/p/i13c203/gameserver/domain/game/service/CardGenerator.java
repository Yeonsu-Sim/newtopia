package io.ssafy.p.i13c203.gameserver.domain.game.service;

import io.ssafy.p.i13c203.gameserver.domain.game.doc.CardDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;

public interface CardGenerator {

    /**
     * 게임 시작 시 최초로 노출되는 카드를 생성한다.
     */
    CardDoc createFirstCard(Long memberId);

    /**
     * 게임이 진행되면서 다음 단계에서 노출될 카드를 생성한다.
     */
    CardDoc createNextCard(Long memberId, Game game);
}
