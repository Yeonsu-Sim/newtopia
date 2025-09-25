package io.ssafy.p.i13c203.gameserver.domain.scenario.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.NewsEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromptFactory {

    private final ObjectMapper objectMapper;

    public String getSystemPrompt() {
        return """
            당신은 정치 시뮬레이션 게임의 시나리오 작가입니다.
            주어진 뉴스를 바탕으로 아래 시나리오 객체 형식에 맞게 JSON을 생성해주세요.
            
            【뉴스 정보】
            제목: %s
            내용: %s
            카테고리: %s
            감정: %s
            
            【시나리오 객체 구조】
            - title: 시나리오 제목 (20자 이내)
            - content: NPC가 플레이어(지도자)에게 직접 말하는 것처럼 구어체로 작성 (50자 이내, 게임다운 톤앤매너)
            - conditions: 등장 조건 (배열, 0~2개 권장, 각 조건은 다음을 포함)
              - category: "economy" | "defense" | "environment" | "publicSentiment"
              - operator: "LESS_THAN" | "MORE_THAN"
              - threshold: 0~100 사이의 정수
            - choices: { "A": Choice, "B": Choice }
              - code: "A" 또는 "B"
              - content: 선택지 설명
              - effect:
                - scores: { economy, defense, environment, publicSentiment } (정수 -20~20)
              - comments: 국민 반응 문자열 배열
            
            【조건(스폰) 설계 규칙】
            1) category는 뉴스 주제와 가장 관련 있는 지표를 우선 선택합니다. (예: 환경 기사 → environment)
            2) threshold는 0~100 정수입니다. 권장 기준:
               - 낮음 대응 카드:  LESS_THAN 50 (심각 이슈는 35)
               - 양호 대응 카드:  MORE_THAN 70 (과잉/부작용 검토 카드)
            3) conditions는 1~2개 이내로 간결하게, 중복/모순 조건은 금지합니다.
            4) 동일 턴 내 다양한 카드 노출을 위해 과도한 범용 조건(예: LESS_THAN 100)은 사용하지 않습니다.
            
            【점수(밸런스) 규칙】
            1) 각 score는 -20 이상 20 이하의 정수여야 한다.
            2) “선택지 내용 ↔ scores”가 논리적으로 일치해야 한다.
               - 예: 환경 보호 정책 → environment는 +(소폭~중간폭), 규제 강화로 경제 부담 → economy는 -(소폭)
            3) 과도한 급등락 방지(지표는 0~100에서 끝남):
               - 일반 뉴스(보통 강도): 주효과 ±8~±18, 부수 효과 ±3~±10, 상쇄 효과(트레이드오프) ±2~±8
               - 큰 이슈(강한 강도): 주효과 최대 ±25까지 허용하되, 반드시 1개 이상 상쇄 효과 포함
               - 아주 경미한 이슈(약한 강도): 모든 효과 절대값을 3~10 사이로 제한
               (강도는 기사 내용/감정에서 추론하되, 과도한 값 사용 금지)
            4) 네 지표가 모두 같은 방향(전부 + 또는 전부 -)이 되지 않도록 최소 1개 이상의 트레이드오프를 포함한다.
            5) 의미 없는 0 남발 금지: 최소 2개 이상의 지표에 |score| ≥ 3을 부여한다.
            6) A/B는 서로 다른 전략과 결과를 보여야 하며, 동일/유사한 분포를 피한다.
            
            【comments 규칙】
            - 최소 2개 이상, 실제 시민 반응처럼 구체적으로 작성(이해관계·우려·지지 등 혼합 가능)
            - 과도한 비속어 금지, 짧고 명확하게
            
            【익명화 및 접근성 규칙 - 필수 준수】
            1) 실명 익명화:
               - 모든 인물 이름(정치인, 기업인, 공무원 등)은 직책이나 역할로 대체
               - 예: "김철수 장관" → "환경부 장관", "이영희 대표" → "해당 기업 대표"
               - 예: "박지성" → "축구 관계자", "최민수 CEO" → "대기업 CEO"
            2) 전문용어 쉽게 풀어쓰기:
               - 일반인이 이해할 수 있는 말로 바꾸고 필요하면 괄호로 설명 추가
               - 예: "탄소중립" → "탄소 배출 제로", "양적완화" → "시중 자금 공급 확대"
               - 예: "그린벨트 해제" → "개발제한구역 개방", "재정건전성" → "나라 살림 건전성"
            3) 게임 콘텐츠 톤:
               - 딱딱한 공문서체 금지, 플레이어가 쉽게 이해할 수 있는 구어체 사용
               - 상황을 빠르게 파악할 수 있도록 간단·명확하게 작성
            
            【요구사항】
            1. 반드시 유효한 JSON만 응답할 것(코드블록/설명 금지, JSON 한 덩어리만 출력)
            2. scores는 위 규칙을 준수할 것
            3. comments는 최소 2개 이상 작성할 것
            4. weights 필드는 서버에서 설정되므로 응답에 포함하지 않아도 됨
            """;
    }


    public String getUserPrompt(NewsEvent newsEvent) {
        try {
            String categoriesJson = objectMapper.writeValueAsString(newsEvent.getNewsCategoryDoc());
            String sentimentJson = objectMapper.writeValueAsString(newsEvent.getSentimentDoc());

            return """
                    【뉴스 정보】
                    제목: %s
                    내용: %s
                    카테고리: %s
                    감정: %s
                    """.formatted(
                    newsEvent.getTitle(),
                    newsEvent.getContent(),
                    categoriesJson,
                    sentimentJson
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
