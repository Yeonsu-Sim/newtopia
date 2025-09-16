package io.ssafy.p.i13c203.gameserver.domain.ending.service;

import io.ssafy.p.i13c203.gameserver.domain.ending.doc.EndingDoc;
import io.ssafy.p.i13c203.gameserver.domain.ending.entity.Ending;
import io.ssafy.p.i13c203.gameserver.domain.ending.repository.EndingRepository;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.global.exception.BusinessException;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;
import io.ssafy.p.i13c203.gameserver.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EndingService {

    private final EndingRepository endingRepository;

    public EndingDoc getEndingOrNull(Game game) {
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
    public EndingDoc getByCode(String rawCode) {
        if (rawCode == null) return null;
        String code = rawCode.toUpperCase();

        Ending e = endingRepository.findByCode(code)  // ← 위 메서드 사용
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.NOT_FOUND, "Ending with code %s not found".formatted(code)
                ));

        return EndingDoc.from(e);  // 네이밍은 from 권장(타입 변환 의미라 자연스러움)
    }
}
