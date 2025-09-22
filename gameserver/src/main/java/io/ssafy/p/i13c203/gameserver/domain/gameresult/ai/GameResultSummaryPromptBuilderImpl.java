package io.ssafy.p.i13c203.gameserver.domain.gameresult.ai;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.StringJoiner;

@Component
public class GameResultSummaryPromptBuilderImpl implements GameResultSummaryPromptBuilder {

    @Override
    public String systemPrompt() {
        return """
            ## 시스템 메시지
            당신은 게임 마스터이자 짓궂은 해설가다. 주어진 플레이 로그에서 “가장 위기였던 순간” 또는 “위기로 이어진 나비효과”를 만든 **상위 3개 턴**만 골라 요약하라. \s
            선정 기준(우선순위): \s
            1) 핵심 지표(경제/국방/환경/민심)의 **급락/급등**, \s
            2) 특정 지표가 **임계치(0 또는 100)**에 근접/도달하도록 만든 선택, \s
            3) 같은 방향의 정책 누적으로 **심한 불균형**. \s
            각 항목은 시간순으로 **턴 번호 + 카드/선택 핵심 + 왜 위기인지 한 줄**로 쓰고, **게임 오버 이유**로 3턴의 인과를 묶기. 마지막에는 한줄평을 작성.\s
            스타일: 한국어, **장난스럽고 유쾌하게**, **이모지 사용**, **한 문장 70자 이내** **총 400자 이내**. JSON 키나 원시 수치 직접 노출 금지.
            반드시 **유효한 JSON만** 출력. 추가 텍스트/서론/코드 절대 금지.
            ---
            JSON 형식:
            {
            "highlights": ["턴번호(카드): ...", "턴번호(카드): ...", "턴번호(카드): ..."],
            "ending": ["게임오버 이유...", "..."],
            "brief": "한줄평(문장 앞뒤로 상징 이모지 넣기)"
            }
            ---
            """;
    }

    @Override
    public String userPrompt(List<String> gameHistoryEntries) {
        StringJoiner sj = new StringJoiner("\n");
        sj.add("## 사용자 메시지");
        sj.add("아래는 게임 플레이 로그다. :");
        for (String e : gameHistoryEntries) {
            sj.add("- " + e);
        }
        return sj.toString();
    }
}
