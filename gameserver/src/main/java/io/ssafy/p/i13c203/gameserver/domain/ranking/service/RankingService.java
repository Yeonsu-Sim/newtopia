package io.ssafy.p.i13c203.gameserver.domain.ranking.service;

import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.ranking.dto.RankingDto;

import java.util.List;

public interface RankingService {

  /**
   * gameId 기반으로 Game 객체를 갖고와 랭킹을 등록합니다.
   * 내부적으로 ended_at이 null인지 확인하지 않습니다.
   * @param gameId
   */
  void registerRanking(Long gameId);

  /**
   * Game 객체를 갖고와 랭킹을 등록합니다.
   * 내부적으로 ended_at이 null인지 확인하지 않습니다.
   * @param game
   */
  void registerRanking(Game game);

  /**
   * gameId 기반으로 해당 게임 객체를 갖고와 랭킹을 조회합니다.
   * @param gameId
   * @return
   */
  RankingDto getRankingByGameId(Long gameId);

  /**
   * userId를 기반으로 해당 유저가 플레이한 모든 게임 기록을 갖고와 각 게임의 랭킹을 조회합니다.
   * @param memberId
   * @return
   */
  List<RankingDto> getRankingByMemberId(Long memberId);

  /**
   * topN의 게임 기록에 대한 랭킹을 갖고옵니다.
   * @param topN
   * @return
   */
  List<RankingDto> getRankingByTopN(Integer topN);
}
