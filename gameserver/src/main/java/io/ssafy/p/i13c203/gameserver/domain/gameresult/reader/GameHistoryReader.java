package io.ssafy.p.i13c203.gameserver.domain.gameresult.reader;

import java.util.List;

/**
 * 게임 히스토리를 단순 문자열로 변환해 프롬프트에 넣을 Reader.
 * turnNumber, 선택 label, 적용 결과 등을 요약.
 */
public interface GameHistoryReader {

    /**
     * 가장 변동 폭이 컸던 10개의 턴 선택 반환
     */
    List<String> readTop10Turns(long gameId);

    /**
     * 가장 변동 폭이 컸던 10개의 턴 선택 반환
     * 단, Event NPC 시나리오는 제외
     */
    List<String> readTop10TurnsNotEvent(long gameId);
}
