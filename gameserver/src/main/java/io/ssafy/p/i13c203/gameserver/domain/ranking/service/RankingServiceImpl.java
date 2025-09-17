package io.ssafy.p.i13c203.gameserver.domain.ranking.service;

import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.game.repository.GameRepository;
import io.ssafy.p.i13c203.gameserver.domain.ranking.dto.RankingDto;
import io.ssafy.p.i13c203.gameserver.domain.ranking.entity.Ranking;
import io.ssafy.p.i13c203.gameserver.domain.ranking.repository.RankingRepository;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;
import io.ssafy.p.i13c203.gameserver.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

  private final GameRepository gameRepository;
  private final RankingRepository rankingRepository;

  @Override
  @Transactional
  public void registerRanking(Long gameId) {
    log.trace("Registering ranking for game {}", gameId);
    var game = gameRepository.findById(gameId).orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));
    var score = game.getTurn() * 1_000_000_000L - game.getEndedAt().getEpochSecond();
    rankingRepository.save(Ranking.builder()
      .score(score)
      .game(game)
      .build());
  }

  @Override
  @Transactional
  public void registerRanking(Game game) {
    log.trace("Registering ranking for game {}", game.getId());
    var score = game.getTurn() * 1_000_000_000L - game.getEndedAt().getEpochSecond();
    rankingRepository.save(Ranking.builder()
      .score(score)
      .game(game)
      .build());
  }

  @Override
  @Transactional(readOnly = true)
  public RankingDto getRankingByGameId(Long memberId) {
    log.trace("Getting ranking for game {}", memberId);
    var row = rankingRepository.findRankingDtoRowByGameId(memberId);
    if (row == null) throw new NotFoundException(ErrorCode.GAME_NOT_FOUND);
    return RankingDto.builder()
      .gameId(row.getGameId())
      .countryName(row.getCountryName())
      .turn(row.getTurn())
      .endedAt(row.getEndedAt())
      .order(row.getOrder())
      .build();
  }

  @Override
  @Transactional(readOnly = true)
  public List<RankingDto> getRankingByMemberId(Long userId) {
    log.trace("Getting ranking for member {}", userId);
    return rankingRepository.findUserRanks(userId).stream()
      .map(r -> RankingDto.builder()
        .gameId(r.getGameId())
        .countryName(r.getCountryName())
        .turn(r.getTurn())
        .endedAt(r.getEndedAt())
        .order(r.getOrder())
        .build())
      .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<RankingDto> getRankingByTopN(Integer topN) {
    log.trace("Getting ranking for topN {}", topN);
    var counter = new AtomicLong(1);
    return rankingRepository.findTopRankings(PageRequest.of(0, topN))
      .stream()
      .map(r -> RankingDto.from(r, counter.getAndIncrement()))
      .toList();
  }
}
