package io.ssafy.p.i13c203.gameserver.domain.gameresult.service;

import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.request.DetailQuery;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.request.ReportQuery;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.response.GameResultDetailResponse;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.response.GameResultReportResponse;

public interface GameResultService {

    /**
     * 엔딩 직후 게임의 결과 컨텍스트를 생성(or 재생성)한다.
     * 기존 레코드가 있으면 갱신하지 않고 그대로 둔다(게임 당 하나).
     */
    void createOnEnding(Long memberId, Game game); // 엔딩 직후 호출

    /**
     * 게임 결과 리포트를 조회한다.
     */
    GameResultReportResponse getReport(Long memberId, Long gameId, ReportQuery query);

    /**
     * 특정 턴의 드릴다운 상세를 조회한다.
     */
    GameResultDetailResponse getTurnDetail(Long memberId, Long gameId, Integer turnNumber, DetailQuery query);

}
