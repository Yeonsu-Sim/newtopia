package io.ssafy.p.i13c203.gameserver.domain.ending.service;

import io.ssafy.p.i13c203.gameserver.domain.ending.doc.EndingDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.global.exception.BusinessException;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class EndingService {

    public EndingDoc getEndingOrNull(Game game) {
        var cs = game.getCountryStats();

        if      (cs.getEconomy()        <= 0)   return getByCode("ECO_MIN");
        else if (cs.getEconomy()        >= 100) return getByCode("ECO_MAX");
        else if (cs.getDefense()        <= 0)   return getByCode("DEF_MIN");
        else if (cs.getDefense()        >= 100) return getByCode("DEF_MAX");
        else if (cs.getPublicSentiment()<= 0)   return getByCode("PUB_MIN");
        else if (cs.getPublicSentiment()>= 100) return getByCode("PUB_MAX");
        else if (cs.getEnvironment()    <= 0)   return getByCode("ENV_MIN");
        else if (cs.getEnvironment()    >= 100) return getByCode("ENV_MAX");

        return null;
    }

    // TODO: DB 연결 후, 하드코딩 테스트 수거
    public EndingDoc getByCode(String code) {
        return switch (code) {
            // 💰 경제
            case "ECO_MAX" -> new EndingDoc("ECO_MAX","돈은 넘쳐났지만, 웃음은 사라졌다.","돈은 넘쳐났지만, 웃음은 사라졌다.","economy==100", "ending/economy_100.png");
            case "ECO_MIN" -> new EndingDoc("ECO_MIN","부국강병? 아니, 그냥 부자국가.","부국강병? 아니, 그냥 부자국가.","economy==0",   "ending/economy_0.png");
            // 🛡️ 국방
            case "DEF_MAX" -> new EndingDoc("DEF_MAX","총과 탱크는 많았지만, 자유는 사라졌다.","총과 탱크는 많았지만, 자유는 사라졌다.","defense==100", "ending/defense_100.png");
            case "DEF_MIN" -> new EndingDoc("DEF_MIN","당신은 장군인가, 지도자인가?","당신은 장군인가, 지도자인가?","defense==0", "ending/defense_0.png");
            // 🗣️ 민심
            case "PUB_MAX" -> new EndingDoc("PUB_MAX","모두가 당신을 사랑했다. 너무 지나치게.","모두가 당신을 사랑했다. 너무 지나치게.","publicSentiment==100","ending/opinion_100.png");
            case "PUB_MIN" -> new EndingDoc("PUB_MIN","민주주의는 박수 소리에 잠식됐다.","민주주의는 박수 소리에 잠식됐다.","publicSentiment==0","ending/opinion_0.png");
            // 🌱 환경
            case "ENV_MAX" -> new EndingDoc("ENV_MAX","숲은 울창했지만, 도시는 텅 비었다.","숲은 울창했지만, 도시는 텅 비었다.","environment==100","ending/environment_100.png");
            case "ENV_MIN" -> new EndingDoc("ENV_MIN","자연은 웃었고, 사람은 울었다.","자연은 웃었고, 사람은 울었다.","environment==0","ending/environment_0.png");
            default -> throw new BusinessException(ErrorCode.NOT_FOUND, "존재하지 않는 엔딩 code: " + code);
        };
    }
}
