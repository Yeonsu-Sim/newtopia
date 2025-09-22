package io.ssafy.p.i13c203.gameserver.domain.gameresult.reader;

import java.util.List;

/**
 * 게임 히스토리를 단순 문자열로 변환해 프롬프트에 넣을 Reader.
 * turnNumber, 선택 label, 적용 결과 등을 요약.
 */
public interface GameHistoryReader {
    List<String> readTop10Turns(long gameId);
}
