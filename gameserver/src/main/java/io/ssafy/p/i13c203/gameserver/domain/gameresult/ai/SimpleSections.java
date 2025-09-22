package io.ssafy.p.i13c203.gameserver.domain.gameresult.ai;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

final class SimpleSections {
    private SimpleSections(){}

    static Map<String,String> split(String text, List<String> keys){
        String pat = "^(?i)(" + String.join("|", keys) + ")\\s*:\\s*$";
        Pattern p = Pattern.compile(pat, Pattern.MULTILINE);
        Matcher m = p.matcher(text);

        Map<String,String> out = new LinkedHashMap<>();
        List<int[]> ranges = new ArrayList<>();
        while (m.find()) ranges.add(new int[]{m.start(), m.end()});

        for (int i = 0; i < ranges.size(); i++) {
            int start = ranges.get(i)[1];
            int end = (i+1 < ranges.size()) ? ranges.get(i+1)[0] : text.length();
            String header = text.substring(ranges.get(i)[0], ranges.get(i)[1]);
            String key = header.replace(":", "").trim().toLowerCase();
            out.put(key, text.substring(start, Math.max(start, end)).trim());
        }
        return out;
    }

    static List<String> toBullets(String body){
        if (body == null || body.isBlank()) return List.of();
        return Arrays.stream(body.split("\\r?\\n"))
                .map(String::trim).filter(s -> !s.isEmpty())
                .map(s -> s.startsWith("- ") ? s.substring(2).trim()
                        : s.startsWith("• ") ? s.substring(2).trim()
                        : s)
                .collect(Collectors.toList());
    }
}
