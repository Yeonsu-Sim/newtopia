package io.ssafy.p.i13c203.gameserver.global.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import static java.util.Map.entry;

@Slf4j
public final class NpcPicker {

    // 카테고리 → 루트 태그 매핑
    private static final Map<String, String> CAT2TAG = Map.ofEntries(
            entry("economy", "경제"),
            entry("defense", "국방"),
            entry("environment", "환경"),
            entry("publicsentiment", "민심"),    // publicSentiment 들어오면 소문자화해서 매칭
            entry("public_sentiment", "민심")
    );

    // 하드코딩된 npcId → 태그셋
    private static final Map<Integer, Set<String>> NPCS = Map.ofEntries(
            entry(18, Set.of("종교")),
            entry(1,  Set.of("경제", "과학")),
            entry(3,  Set.of("경제", "노동")),
            entry(2,  Set.of("경제", "직장인")),
            entry(6,  Set.of("국방", "요원")),
            entry(13, Set.of("민심", "가정")),
            entry(12, Set.of("민심", "시민")),
            entry(14, Set.of("민심", "아동")),
            entry(11, Set.of("민심", "학생")),
            entry(10, Set.of("민심", "학생")),
            entry(16, Set.of("언론", "미디어")),
            entry(17, Set.of("의료", "보건")),
            entry(19, Set.of("취업", "학생")),
            entry(7,  Set.of("환경", "농업")),
            entry(8,  Set.of("환경", "동물")),
            entry(4,  Set.of("경제", "정부", "장관")),
            entry(5,  Set.of("국방", "정부", "장관")),
            entry(15, Set.of("민심", "노동", "대표")),
            entry(20, Set.of("언론", "외교", "정부")),
            entry(9,  Set.of("환경", "정부", "청장"))
    );

    /** 카테고리로 관련 NPC를 랜덤 1명 선택해서 id 반환 */
    public static int pick(String category) {
        log.info("NPC 선택 요청 - 입력 카테고리: {}", category);

        if (category == null || category.isBlank()) {
            int selectedId = randomFromAll();
            log.info("빈 카테고리 → 전체에서 랜덤 선택: NPC ID = {}", selectedId);
            return selectedId;
        }

        String key = category.toLowerCase(Locale.ROOT);
        String rootTag = CAT2TAG.get(key);

        if (rootTag == null) {
            int selectedId = randomFromAll();
            log.info("알 수 없는 카테고리 '{}' → 전체에서 랜덤 선택: NPC ID = {}", category, selectedId);
            return selectedId;
        }

        log.info("카테고리 '{}' → 태그 '{}' 매핑됨", category, rootTag);

        List<Integer> candidates = new ArrayList<>();
        for (var e : NPCS.entrySet()) {
            Set<String> npcTags = e.getValue();
            // 루트 태그가 포함되어 있거나, 카테고리별 세부 태그들이 매칭되면 후보에 추가
            if (npcTags.contains(rootTag) || hasRelatedTags(category, npcTags)) {
                candidates.add(e.getKey());
            }
        }

        if (candidates.isEmpty()) {
            int selectedId = randomFromAll();
            log.warn("태그 '{}' 매칭 NPC 없음 → 전체에서 랜덤 선택: NPC ID = {}", rootTag, selectedId);
            return selectedId;
        }

        int selectedId = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        log.info("카테고리 '{}' (태그: '{}') → {} 명 후보 중 선택: NPC ID = {}, 후보 목록: {}",
                category, rootTag, candidates.size(), selectedId, candidates);

        return selectedId;
    }

    /** 카테고리별 세부 태그들이 매칭되는지 확인 */
    private static boolean hasRelatedTags(String category, Set<String> npcTags) {
        String key = category.toLowerCase(Locale.ROOT);

        // 각 카테고리별로 관련 태그들을 확인
        switch (key) {
            case "economy":
                return npcTags.contains("과학") || npcTags.contains("노동") ||
                       npcTags.contains("직장인") || npcTags.contains("정부") ||
                       npcTags.contains("장관");

            case "defense":
                return npcTags.contains("요원") || npcTags.contains("정부") ||
                       npcTags.contains("장관");

            case "environment":
                return npcTags.contains("농업") || npcTags.contains("동물") ||
                       npcTags.contains("정부") || npcTags.contains("청장");

            case "publicsentiment":
            case "public_sentiment":
                return npcTags.contains("가정") || npcTags.contains("시민") ||
                       npcTags.contains("아동") || npcTags.contains("학생") ||
                       npcTags.contains("노동") || npcTags.contains("대표");

            default:
                return false;
        }
    }

    private static int randomFromAll() {
        List<Integer> all = new ArrayList<>(NPCS.keySet());
        int selectedId = all.get(ThreadLocalRandom.current().nextInt(all.size()));
        log.debug("전체 NPC 중 랜덤 선택: {} (전체 {}명 중)", selectedId, all.size());
        return selectedId;
    }

    // 데모
    public static void main(String[] args) {
        System.out.println("economy -> " + pick("economy"));
        System.out.println("defense -> " + pick("defense"));
        System.out.println("environment -> " + pick("environment"));
        System.out.println("publicSentiment -> " + pick("publicSentiment"));
    }
}
