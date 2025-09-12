package io.ssafy.p.i13c203.gameserver.domain.scenario.service;

import io.ssafy.p.i13c203.gameserver.domain.game.doc.ConditionEntryDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.EffectDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.EffectScoresDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.EffectWeightsDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.model.ConditionOperator;
import io.ssafy.p.i13c203.gameserver.domain.game.model.MinorCategory;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.ChoiceDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.RelatedArticleDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.SpawnConditionsDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Npc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import io.ssafy.p.i13c203.gameserver.domain.scenario.repository.NpcRepository;
import io.ssafy.p.i13c203.gameserver.domain.scenario.repository.ScenarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioDataLoader implements CommandLineRunner {

    private final NpcRepository npcRepository;
    private final ScenarioRepository scenarioRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting to load scenario seed data...");
        
        loadNpcs();
        loadScenarios();
        
        log.info("Scenario seed data loading completed.");
    }

    private void loadNpcs() {
        log.info("Loading NPCs...");
        
        createOrUpdateNpc(101L, "대변인", "npc/spokesperson.png");
        createOrUpdateNpc(102L, "재정경제부 장관", "npc/finance_minister.png");
        createOrUpdateNpc(103L, "국방부 장관", "npc/defense_minister.png");
        createOrUpdateNpc(104L, "환경청 청장", "npc/environment_agency.png");
        createOrUpdateNpc(105L, "노동단체 대표", "npc/labor_leader.png");

        log.info("Loaded 5 NPCs");
    }

    private void createOrUpdateNpc(Long id, String name, String imageS3Key) {
        Optional<Npc> existingNpc = npcRepository.findById(id);
        
        if (existingNpc.isPresent()) {
            Npc npc = existingNpc.get();
            npc.setName(name);
            npc.setImageS3Key(imageS3Key);
            npcRepository.save(npc);
        } else {
            // Native SQL을 사용하여 ID를 포함한 INSERT 수행
            entityManager.createNativeQuery(
                "INSERT INTO npc (id, name, image_s3_key, created_at, updated_at) " +
                "VALUES (?, ?, ?, now(), now()) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "name = EXCLUDED.name, " +
                "image_s3_key = EXCLUDED.image_s3_key, " +
                "updated_at = now()"
            )
            .setParameter(1, id)
            .setParameter(2, name)
            .setParameter(3, imageS3Key)
            .executeUpdate();
        }
    }

    private void loadScenarios() {
        log.info("Loading scenarios...");
        
        List<Scenario> scenarios = List.of(
            createScenario1(),
            createScenario2(),
            createScenario3(),
            createScenario4(),
            createScenario5(),
            createScenario6(),
            createScenario7(),
            createScenario8(),
            createScenario9(),
            createScenario10()
        );

        for (Scenario scenario : scenarios) {
            createOrUpdateScenario(scenario);
        }
        
        log.info("Loaded {} scenarios", scenarios.size());
    }

    private void createOrUpdateScenario(Scenario scenario) {
        Optional<Scenario> existing = scenarioRepository.findById(scenario.getId());
        if (existing.isPresent()) {
            Scenario existingScenario = existing.get();
            existingScenario.setTitle(scenario.getTitle());
            existingScenario.setContent(scenario.getContent());
            existingScenario.setNpc(scenario.getNpc());
            existingScenario.setSpawn(scenario.getSpawn());
            existingScenario.setChoices(scenario.getChoices());
            existingScenario.setRelatedArticle(scenario.getRelatedArticle());
            scenarioRepository.save(existingScenario);
        } else {
            // Native SQL을 사용하여 ID를 포함한 INSERT 수행
            String sql = "INSERT INTO scenario (id, title, content, npc_id, spawn, choices, related_article, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?::jsonb, ?::jsonb, ?::jsonb, now(), now()) " +
                        "ON CONFLICT (id) DO UPDATE SET " +
                        "title = EXCLUDED.title, " +
                        "content = EXCLUDED.content, " +
                        "npc_id = EXCLUDED.npc_id, " +
                        "spawn = EXCLUDED.spawn, " +
                        "choices = EXCLUDED.choices, " +
                        "related_article = EXCLUDED.related_article, " +
                        "updated_at = now()";
                        
            entityManager.createNativeQuery(sql)
                .setParameter(1, scenario.getId())
                .setParameter(2, scenario.getTitle())
                .setParameter(3, scenario.getContent())
                .setParameter(4, scenario.getNpc().getId())
                .setParameter(5, convertToJsonString(scenario.getSpawn()))
                .setParameter(6, convertToJsonString(scenario.getChoices()))
                .setParameter(7, convertToJsonString(scenario.getRelatedArticle()))
                .executeUpdate();
        }
    }

    private String convertToJsonString(Object obj) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }

    private Scenario createScenario1() {
        Npc npc = npcRepository.findById(101L).orElseThrow();
        
        SpawnConditionsDoc spawn = new SpawnConditionsDoc(List.of());
        
        Map<String, ChoiceDoc> choices = Map.of(
            "A", new ChoiceDoc(
                "A",
                "일자리 창출 계획 가속",
                new EffectDoc(
                    new EffectScoresDoc(3, 0, 1, 0),
                    new EffectWeightsDoc(0.08, 0.0, 0.0, 0.12, 0.0, 0.0, 0.0, 0.0, 0.04, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                )
            ),
            "B", new ChoiceDoc(
                "B",
                "재정 건전성 최우선",
                new EffectDoc(
                    new EffectScoresDoc(1, 0, 0, 0),
                    new EffectWeightsDoc(0.0, 0.18, 0.08, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                )
            )
        );
        
        RelatedArticleDoc relatedArticle = new RelatedArticleDoc("경제 컨퍼런스 개최", "https://news.example/econ-1");
        
        return Scenario.builder()
            .id(1L)
            .title("새 정부의 경제 어젠다")
            .content("새로운 내각이 경제정책 우선순위를 논의합니다.")
            .npc(npc)
            .spawn(spawn)
            .choices(choices)
            .relatedArticle(relatedArticle)
            .build();
    }

    private Scenario createScenario2() {
        Npc npc = npcRepository.findById(103L).orElseThrow();
        
        SpawnConditionsDoc spawn = new SpawnConditionsDoc(List.of(
            new ConditionEntryDoc(MinorCategory.alliances, ConditionOperator.MORE_THAN, 0.2)
        ));
        
        Map<String, ChoiceDoc> choices = Map.of(
            "A", new ChoiceDoc(
                "A",
                "훈련 재개",
                new EffectDoc(
                    new EffectScoresDoc(0, 4, -1, -1),
                    new EffectWeightsDoc(0.0, 0.0, 0.0, 0.0, 0.12, 0.10, 0.0, 0.05, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                )
            ),
            "B", new ChoiceDoc(
                "B",
                "외교 우선 접근",
                new EffectDoc(
                    new EffectScoresDoc(0, -1, 2, 0),
                    new EffectWeightsDoc(0.0, 0.0, 0.0, 0.0, 0.0, 0.06, 0.0, 0.0, 0.08, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                )
            )
        );
        
        RelatedArticleDoc relatedArticle = new RelatedArticleDoc("국방예산 심의 착수", "https://news.example/def-1");
        
        return Scenario.builder()
            .id(2L)
            .title("합동 군사훈련 재개 여부")
            .content("인접국과의 긴장 속 군사훈련 재개를 검토합니다.")
            .npc(npc)
            .spawn(spawn)
            .choices(choices)
            .relatedArticle(relatedArticle)
            .build();
    }

    private Scenario createScenario3() {
        Npc npc = npcRepository.findById(101L).orElseThrow();
        
        SpawnConditionsDoc spawn = new SpawnConditionsDoc(List.of(
            new ConditionEntryDoc(MinorCategory.socialIssues, ConditionOperator.MORE_THAN, 0.15)
        ));
        
        Map<String, ChoiceDoc> choices = Map.of(
            "A", new ChoiceDoc(
                "A",
                "공공임대 확대",
                new EffectDoc(
                    new EffectScoresDoc(-1, 0, 3, 0),
                    new EffectWeightsDoc(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.10, 0.12, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                )
            ),
            "B", new ChoiceDoc(
                "B",
                "세제 지원 중심",
                new EffectDoc(
                    new EffectScoresDoc(1, 0, 1, 0),
                    new EffectWeightsDoc(0.0, 0.10, 0.05, 0.0, 0.0, 0.0, 0.0, 0.0, 0.04, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                )
            )
        );
        
        RelatedArticleDoc relatedArticle = new RelatedArticleDoc("주거안정 입법 발의", "https://news.example/soc-1");
        
        return Scenario.builder()
            .id(3L)
            .title("주거비 안정 대책")
            .content("청년·신혼부부 주거비 완화를 위한 대책을 논의합니다.")
            .npc(npc)
            .spawn(spawn)
            .choices(choices)
            .relatedArticle(relatedArticle)
            .build();
    }

    private Scenario createScenario4() {
        Npc npc = npcRepository.findById(104L).orElseThrow();
        
        SpawnConditionsDoc spawn = new SpawnConditionsDoc(List.of(
            new ConditionEntryDoc(MinorCategory.climateChangeEnergy, ConditionOperator.MORE_THAN, 0.1)
        ));
        
        Map<String, ChoiceDoc> choices = Map.of(
            "A", new ChoiceDoc(
                "A",
                "2030 조기 감축",
                new EffectDoc(
                    new EffectScoresDoc(-2, 0, 1, 4),
                    new EffectWeightsDoc(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.15, 0.0, 0.05, 0.0)
                )
            ),
            "B", new ChoiceDoc(
                "B",
                "점진적 감축",
                new EffectDoc(
                    new EffectScoresDoc(0, 0, 0, 2),
                    new EffectWeightsDoc(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.08, 0.0, 0.06)
                )
            )
        );
        
        RelatedArticleDoc relatedArticle = new RelatedArticleDoc("RE100 기업 확대", "https://news.example/env-1");
        
        return Scenario.builder()
            .id(4L)
            .title("석탄발전 축소 로드맵")
            .content("온실가스 감축을 위한 석탄발전 축소 방안을 검토합니다.")
            .npc(npc)
            .spawn(spawn)
            .choices(choices)
            .relatedArticle(relatedArticle)
            .build();
    }

    private Scenario createScenario5() {
        Npc npc = npcRepository.findById(103L).orElseThrow();
        
        SpawnConditionsDoc spawn = new SpawnConditionsDoc(List.of(
            new ConditionEntryDoc(MinorCategory.publicSafety, ConditionOperator.LESS_THAN, 0.6)
        ));
        
        Map<String, ChoiceDoc> choices = Map.of(
            "A", new ChoiceDoc(
                "A",
                "대규모 초기 투자",
                new EffectDoc(
                    new EffectScoresDoc(-1, 3, 1, 0),
                    new EffectWeightsDoc(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.12, 0.10, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                )
            ),
            "B", new ChoiceDoc(
                "B",
                "파일럿 후 단계적 확대",
                new EffectDoc(
                    new EffectScoresDoc(0, 1, 0, 0),
                    new EffectWeightsDoc(0.0, 0.0, 0.0, 0.06, 0.0, 0.0, 0.06, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                )
            )
        );
        
        RelatedArticleDoc relatedArticle = new RelatedArticleDoc("재난망 테스트 결과", "https://news.example/space-1");
        
        return Scenario.builder()
            .id(5L)
            .title("위성 통신망 투자")
            .content("재난 대비 위성 기반 통신망 투자 여부를 결정합니다.")
            .npc(npc)
            .spawn(spawn)
            .choices(choices)
            .relatedArticle(relatedArticle)
            .build();
    }

    private Scenario createScenario6() {
        Npc npc = npcRepository.findById(105L).orElseThrow();
        
        SpawnConditionsDoc spawn = new SpawnConditionsDoc(List.of(
            new ConditionEntryDoc(MinorCategory.macroeconomy, ConditionOperator.LESS_THAN, 0.7)
        ));
        
        Map<String, ChoiceDoc> choices = Map.of(
            "A", new ChoiceDoc(
                "A",
                "인상 폭 확대",
                new EffectDoc(
                    new EffectScoresDoc(-1, 0, 3, 0),
                    new EffectWeightsDoc(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.10, 0.0, 0.10, 0.0, 0.0, 0.0, 0.0, 0.0)
                )
            ),
            "B", new ChoiceDoc(
                "B",
                "점진적 인상",
                new EffectDoc(
                    new EffectScoresDoc(0, 0, 1, 0),
                    new EffectWeightsDoc(0.0, 0.0, 0.0, 0.06, 0.0, 0.0, 0.0, 0.0, 0.05, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                )
            )
        );
        
        RelatedArticleDoc relatedArticle = new RelatedArticleDoc("노사정 위원회 재가동", "https://news.example/labor-1");
        
        return Scenario.builder()
            .id(6L)
            .title("최저임금 조정 논의")
            .content("물가·고용 상황을 고려해 최저임금 조정을 검토합니다.")
            .npc(npc)
            .spawn(spawn)
            .choices(choices)
            .relatedArticle(relatedArticle)
            .build();
    }

    private Scenario createScenario7() {
        Npc npc = npcRepository.findById(101L).orElseThrow();
        
        SpawnConditionsDoc spawn = new SpawnConditionsDoc(List.of(
            new ConditionEntryDoc(MinorCategory.healthWelfare, ConditionOperator.LESS_THAN, 0.5)
        ));
        
        Map<String, ChoiceDoc> choices = Map.of(
            "A", new ChoiceDoc(
                "A",
                "공공의대 신설",
                new EffectDoc(
                    new EffectScoresDoc(-1, 0, 3, 0),
                    new EffectWeightsDoc(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.05, 0.0, 0.15, 0.0, 0.0, 0.0, 0.0)
                )
            ),
            "B", new ChoiceDoc(
                "B",
                "지역가산·인센티브",
                new EffectDoc(
                    new EffectScoresDoc(0, 0, 2, 0),
                    new EffectWeightsDoc(0.0, 0.0, 0.05, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.10, 0.0, 0.0, 0.0, 0.0)
                )
            )
        );
        
        RelatedArticleDoc relatedArticle = new RelatedArticleDoc("지역 응급 인력난 심화", "https://news.example/health-1");
        
        return Scenario.builder()
            .id(7L)
            .title("필수의료 인력 확충")
            .content("지방 필수의료 공백 해소를 위해 인력 확충을 추진합니다.")
            .npc(npc)
            .spawn(spawn)
            .choices(choices)
            .relatedArticle(relatedArticle)
            .build();
    }

    private Scenario createScenario8() {
        Npc npc = npcRepository.findById(102L).orElseThrow();
        
        SpawnConditionsDoc spawn = new SpawnConditionsDoc(List.of(
            new ConditionEntryDoc(MinorCategory.industryBusiness, ConditionOperator.MORE_THAN, 0.1)
        ));
        
        Map<String, ChoiceDoc> choices = Map.of(
            "A", new ChoiceDoc(
                "A",
                "세액공제 확대",
                new EffectDoc(
                    new EffectScoresDoc(4, 0, 0, -1),
                    new EffectWeightsDoc(0.0, 0.0, 0.06, 0.15, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                )
            ),
            "B", new ChoiceDoc(
                "B",
                "엄격한 성과조건",
                new EffectDoc(
                    new EffectScoresDoc(2, 0, 0, 0),
                    new EffectWeightsDoc(0.0, 0.08, 0.0, 0.08, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                )
            )
        );
        
        RelatedArticleDoc relatedArticle = new RelatedArticleDoc("수출 회복 신호", "https://news.example/ind-1");
        
        return Scenario.builder()
            .id(8L)
            .title("반도체 클러스터 세액공제")
            .content("대규모 반도체 클러스터 유치 세액공제를 검토합니다.")
            .npc(npc)
            .spawn(spawn)
            .choices(choices)
            .relatedArticle(relatedArticle)
            .build();
    }

    private Scenario createScenario9() {
        Npc npc = npcRepository.findById(104L).orElseThrow();
        
        SpawnConditionsDoc spawn = new SpawnConditionsDoc(List.of(
            new ConditionEntryDoc(MinorCategory.pollutionDisaster, ConditionOperator.MORE_THAN, 0.15)
        ));
        
        Map<String, ChoiceDoc> choices = Map.of(
            "A", new ChoiceDoc(
                "A",
                "선제적 대규모 투자",
                new EffectDoc(
                    new EffectScoresDoc(-1, 0, 2, 3),
                    new EffectWeightsDoc(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.10, 0.0, 0.0, 0.0, 0.0, 0.0, 0.12, 0.0, 0.06)
                )
            ),
            "B", new ChoiceDoc(
                "B",
                "지자체 매칭 중심",
                new EffectDoc(
                    new EffectScoresDoc(0, 0, 1, 2),
                    new EffectWeightsDoc(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.06, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.06)
                )
            )
        );
        
        RelatedArticleDoc relatedArticle = new RelatedArticleDoc("기후위기 대응 예산 확대", "https://news.example/safety-1");
        
        return Scenario.builder()
            .id(9L)
            .title("홍수 취약지 개선사업")
            .content("우기 대비 하천 정비와 배수 인프라 투자를 검토합니다.")
            .npc(npc)
            .spawn(spawn)
            .choices(choices)
            .relatedArticle(relatedArticle)
            .build();
    }

    private Scenario createScenario10() {
        Npc npc = npcRepository.findById(103L).orElseThrow();
        
        SpawnConditionsDoc spawn = new SpawnConditionsDoc(List.of(
            new ConditionEntryDoc(MinorCategory.alliances, ConditionOperator.MORE_THAN, 0.1),
            new ConditionEntryDoc(MinorCategory.publicOpinion, ConditionOperator.MORE_THAN, 0.05)
        ));
        
        Map<String, ChoiceDoc> choices = Map.of(
            "A", new ChoiceDoc(
                "A",
                "정식 가입 추진",
                new EffectDoc(
                    new EffectScoresDoc(0, 3, 1, 0),
                    new EffectWeightsDoc(0.0, 0.0, 0.0, 0.0, 0.06, 0.14, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                )
            ),
            "B", new ChoiceDoc(
                "B",
                "옵저버로 참여",
                new EffectDoc(
                    new EffectScoresDoc(0, 1, 0, 0),
                    new EffectWeightsDoc(0.0, 0.0, 0.0, 0.0, 0.0, 0.08, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                )
            )
        );
        
        RelatedArticleDoc relatedArticle = new RelatedArticleDoc("다자외교 복원", "https://news.example/diplo-1");
        
        return Scenario.builder()
            .id(10L)
            .title("다자안보 협의체 가입")
            .content("새로운 다자안보 협의체 참여를 검토합니다.")
            .npc(npc)
            .spawn(spawn)
            .choices(choices)
            .relatedArticle(relatedArticle)
            .build();
    }
}