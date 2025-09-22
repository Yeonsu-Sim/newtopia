package io.ssafy.p.i13c203.gameserver.domain.gameresult.ai;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SummaryMessageParser {

    private final ObjectMapper om; // @Bean 으로 기본 등록된 ObjectMapper 사용

    public SummarySections parse(String raw) {
        if (raw == null || raw.isBlank()) return SummarySections.empty();

        String text = stripCodeFences(raw).trim();

        SummaryPayload p = tryParseJson(text)
                .orElseGet(() -> parseLax(text));

        return toSections(p);
    }

    /* ---------- JSON ---------- */
    private Optional<SummaryPayload> tryParseJson(String text) {
        try {
            JsonNode root = om.readTree(text);
            return Optional.of(new SummaryPayload(
                    readArray(root.get("highlights")),
                    readArray(root.get("ending")),
                    readString(root.get("brief"))
            ));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /* ---------- Lax (fallback) ---------- */
    private SummaryPayload parseLax(String text) {
        // 아주 간단한 섹션 나누기: 'highlights:' / 'ending:' / 'brief:'
        Map<String, String> sec = SimpleSections.split(text, List.of("highlights","ending","brief"));
        return new SummaryPayload(
                SimpleSections.toBullets(sec.get("highlights")),
                SimpleSections.toBullets(sec.get("ending")),
                Optional.ofNullable(sec.get("brief")).map(String::trim).orElse(null)
        );
    }

    /* ---------- Assemble ---------- */
    private SummarySections toSections(SummaryPayload p) {
        SummarySections s = new SummarySections();
        if (!p.highlights().isEmpty())
            s.put("highlights", BulletsBlock.of("하이라이트 턴", p.highlights()));
        if (!p.ending().isEmpty())
            s.put("ending", BulletsBlock.of("결말", p.ending()));
        if (p.brief() != null && !p.brief().isBlank())
            s.put("brief", TextBlock.of("한줄평", p.brief()));
        return s;
    }

    /* ---------- utils ---------- */
    private static String stripCodeFences(String s) {
        s = s.trim();
        if (s.startsWith("```")) {
            int nl = s.indexOf('\n');
            if (nl > 0) s = s.substring(nl + 1);
            int last = s.lastIndexOf("```");
            if (last >= 0) s = s.substring(0, last);
        }
        return s;
    }
    private static List<String> readArray(JsonNode n) {
        if (n == null || n.isNull()) return List.of();
        if (n.isArray()) {
            ArrayNode arr = (ArrayNode) n;
            List<String> out = new ArrayList<>(arr.size());
            arr.forEach(e -> { if (e != null && !e.isNull()) out.add(e.asText()); });
            return out.stream().map(String::trim).filter(s -> !s.isEmpty()).toList();
        }
        if (n.isTextual()) {
            return Arrays.stream(n.asText().split("\\r?\\n"))
                    .map(String::trim).filter(s -> !s.isEmpty()).toList();
        }
        return List.of();
    }
    private static String readString(JsonNode n) {
        if (n == null || n.isNull()) return null;
        String v = n.asText();
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    /* 작은 VO */
    private record SummaryPayload(List<String> highlights, List<String> ending, String brief) {}
}
