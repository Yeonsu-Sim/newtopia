package io.ssafy.p.i13c203.gameserver.domain.ending.service;

import io.ssafy.p.i13c203.gameserver.domain.ending.dto.EndingAssetsDto;
import io.ssafy.p.i13c203.gameserver.domain.ending.dto.response.EndingDetailResponse;
import io.ssafy.p.i13c203.gameserver.domain.ending.dto.response.GetMyEndingsResponse;
import io.ssafy.p.i13c203.gameserver.domain.ending.dto.response.GetMyEndingsResponse.EndingStatus;
import io.ssafy.p.i13c203.gameserver.domain.ending.entity.Ending;
import io.ssafy.p.i13c203.gameserver.domain.ending.repository.EndingRepository;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.game.repository.GameRepository;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;
import io.ssafy.p.i13c203.gameserver.global.exception.NotFoundException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EndingService {

    private final EndingRepository endingRepository;
    private final GameRepository gameRepository;

    public Ending getEndingOrNull(Game game) {
        var cs = game.getCountryStats();
        List<String> hitCodes = new ArrayList<>();

        if (cs.getEconomy()        <= 0)   hitCodes.add("ECO_MIN");
        if (cs.getEconomy()        >= 100) hitCodes.add("ECO_MAX");
        if (cs.getDefense()        <= 0)   hitCodes.add("DEF_MIN");
        if (cs.getDefense()        >= 100) hitCodes.add("DEF_MAX");
        if (cs.getPublicSentiment()<= 0)   hitCodes.add("PUB_MIN");
        if (cs.getPublicSentiment()>= 100) hitCodes.add("PUB_MAX");
        if (cs.getEnvironment()    <= 0)   hitCodes.add("ENV_MIN");
        if (cs.getEnvironment()    >= 100) hitCodes.add("ENV_MAX");

        int n = hitCodes.size();
        if (n == 0) return null;

        String code = switch (n) {
            case 1 -> hitCodes.get(0);
            case 2 -> "DOUBLE_OVER";
            case 3 -> "TRIPLE_OVER";
            case 4 -> "QUAD_OVER";
            default -> null;
        };
        return getByCode(code);
    }

    @Transactional(readOnly = true)
    public Ending getByCode(String rawCode) {
        if (rawCode == null) return null;
        String code = rawCode.toUpperCase();

        Ending ending = endingRepository.findByCode(code)  // ← 위 메서드 사용
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.NOT_FOUND, "Ending with code %s not found".formatted(code)
                ));

        return ending;
    }

    @Transactional(readOnly = true)
    public GetMyEndingsResponse getMyEndings(Long memberId) {
        // 1. 전체 엔딩 목록 조회
        List<Ending> endings = endingRepository.findAllByOrderByIdAsc();  // ID 오름차순 정렬

        // 2. memberId별 집계 조회
        Map<String, GameRepository.EndingCountProjection> collectedMap =
                gameRepository.countEndingsByMember(memberId).stream()
                        .collect(Collectors.toMap(
                                GameRepository.EndingCountProjection::getCode,
                                projection -> projection
                                                 ));

        log.debug("my ending collections: {}", collectedMap);

        // 3. merge + DTO 변환
        List<GetMyEndingsResponse.Ending> endingDtos = endings.stream()
                .map(e -> {
                    GameRepository.EndingCountProjection projection =
                            collectedMap.get(e.getCode());

                    int count = projection != null ? projection.getCnt() : 0;
                    LocalDateTime lastCollectedAt = projection != null ? projection.getLastCollectedAt() : null;

                    return GetMyEndingsResponse.Ending.builder()
                            .code(e.getCode())
                            .title(e.getTitle())
                            .content(e.getContent())
                            .assets(EndingAssetsDto.from(e))
                            .status(EndingStatus.builder()
                                    .collected(count > 0)
                                    .count(count)
                                    .lastCollectedAt(lastCollectedAt)
                                    .build())
                            .build();
                })
                // TODO: (수집 횟수 -> 최신 수집) 우선순위 정렬 수거 보류
//                .sorted(Comparator
//                        .comparingInt((GetMyEndingsResponse.Ending dto) -> dto.getStatus().getCount())
//                        .reversed()
//                        .thenComparing(dto -> dto.getStatus().getLastCollectedAt(),
//                                Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        // 4. summary
        GetMyEndingsResponse.Summary summary = GetMyEndingsResponse.Summary.builder()
                .total(endings.size())
                .collected((int) collectedMap.size())
                .build();

        return GetMyEndingsResponse.builder()
                .summary(summary)
                .endings(endingDtos)
                .build();
    }
}
