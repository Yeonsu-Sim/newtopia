-- ====================================================================
-- File: data.sql
-- Purpose: 임시(로컬/테스트용) 시드 데이터 일괄 삽입
-- Note:
--   - 기존에 넣어둔 시드 데이터는 모두 제거 후 새로 삽입
-- ====================================================================

-- 기존 데이터 삭제
TRUNCATE TABLE public.scenario RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.npc RESTART IDENTITY CASCADE;

-- --------------------------------------------------------------------
-- NPC 시드 데이터
-- --------------------------------------------------------------------
-- 1: 대변인
INSERT INTO public.npc (created_at, id, updated_at, name, image_s3_key)
VALUES (now(), 1, now(), '대변인', 'npc/spokesperson.png');

-- 2: 재정경제부 장관
INSERT INTO public.npc (created_at, id, updated_at, name, image_s3_key)
VALUES (now(), 2, now(), '재정경제부 장관', 'npc/finance_minister.png');

-- 3: 국방부 장관
INSERT INTO public.npc (created_at, id, updated_at, name, image_s3_key)
VALUES (now(), 3, now(), '국방부 장관', 'npc/defense_minister.png');

-- 4: 환경청 청장
INSERT INTO public.npc (created_at, id, updated_at, name, image_s3_key)
VALUES (now(), 4, now(), '환경청 청장', 'npc/environment_agency.png');

-- 5: 노동단체 대표
INSERT INTO public.npc (created_at, id, updated_at, name, image_s3_key)
VALUES (now(), 5, now(), '노동단체 대표', 'npc/labor_leader.png');

-- --------------------------------------------------------------------
-- Scenario 시드 데이터
--   컬럼: id, npc_id(FK), title, content
--   choices/related_article/spawn 은 JSON(B) 컬럼 가정
-- --------------------------------------------------------------------

-- 1: 새 정부의 경제 어젠다 (NPC: 대변인=1)
INSERT INTO public.scenario
(created_at, id, npc_id, updated_at, title, content, choices, related_article, spawn)
VALUES (
           now(),
           1,
           1,
           now(),
           '새 정부의 경제 어젠다',
           '새로운 내각이 경제정책 우선순위를 논의합니다.',
           '{"A": {"code": "A", "label": "일자리 창출 계획 가속", "effect": {"scores": {"defense": 0, "economy": 3, "environment": 0, "publicSentiment": 100}, "weights": {"alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0, "macroeconomy": 0.8, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0, "publicOpinion": 0.4, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.12, "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0}}}, "B": {"code": "B", "label": "재정 건전성 최우선", "effect": {"scores": {"defense": 0, "economy": 10, "environment": 0, "publicSentiment": 0}, "weights": {"alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.18, "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0, "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.8, "industryBusiness": 0.0, "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0}}}}',
           '{"url": "https://news.example/econ-1", "title": "경제 컨퍼런스 개최"}',
           '{"conditions": []}'
       );

-- 2: 합동 군사훈련 재개 여부 (NPC: 국방부 장관=3)
INSERT INTO public.scenario
(created_at, id, npc_id, updated_at, title, content, choices, related_article, spawn)
VALUES (
           now(),
           2,
           3,
           now(),
           '합동 군사훈련 재개 여부',
           '인접국과의 긴장 속 군사훈련 재개를 검토합니다.',
           '{"A": {"code": "A", "label": "훈련 재개", "effect": {"scores": {"defense": 4, "economy": 0, "environment": -1, "publicSentiment": -10}, "weights": {"alliances": 0.1, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0, "macroeconomy": 0.0, "publicSafety": 0.5, "socialIssues": 0.0, "healthWelfare": 0.0, "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0, "militarySecurity": 0.12, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0}}}, "B": {"code": "B", "label": "외교 우선 접근", "effect": {"scores": {"defense": -10, "economy": 0, "environment": 0, "publicSentiment": 20}, "weights": {"alliances": 0.6, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0, "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0, "publicOpinion": 0.8, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0, "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0}}}}',
           '{"url": "https://news.example/def-1", "title": "국방예산 심의 착수"}',
           '{"conditions": [{"category": "alliances", "operator": "MORE_THAN", "threshold": 0.2}]}'
       );

-- 3: 주거비 안정 대책 (NPC: 대변인=1)
INSERT INTO public.scenario
(created_at, id, npc_id, updated_at, title, content, choices, related_article, spawn)
VALUES (
           now(),
           3,
           1,
           now(),
           '주거비 안정 대책',
           '청년·신혼부부 주거비 완화를 위한 대책을 논의합니다.',
           '{"A": {"code": "A", "label": "공공임대 확대", "effect": {"scores": {"defense": 0, "economy": -10, "environment": 0, "publicSentiment": 30}, "weights": {"alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0, "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.12, "healthWelfare": 0.0, "publicOpinion": 0.1, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0, "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0}}}, "B": {"code": "B", "label": "세제 지원 중심", "effect": {"scores": {"defense": 0, "economy": 10, "environment": 0, "publicSentiment": 10}, "weights": {"alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.1, "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0, "publicOpinion": 0.4, "protestsStrikes": 0.0, "financialMarkets": 0.5, "industryBusiness": 0.0, "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0}}}}',
           '{"url": "https://news.example/soc-1", "title": "주거안정 입법 발의"}',
           '{"conditions": [{"category": "socialIssues", "operator": "MORE_THAN", "threshold": 0.15}]}'
       );

-- 4: 석탄발전 축소 로드맵 (NPC: 환경청 청장=4)
INSERT INTO public.scenario
(created_at, id, npc_id, updated_at, title, content, choices, related_article, spawn)
VALUES (
           now(),
           4,
           4,
           now(),
           '석탄발전 축소 로드맵',
           '온실가스 감축을 위한 석탄발전 축소 방안을 검토합니다.',
           '{"A": {"code": "A", "label": "2030 조기 감축", "effect": {"scores": {"defense": 0, "economy": -20, "environment": 4, "publicSentiment": 100}, "weights": {"alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.5, "fiscalPolicy": 0.0, "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0, "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0, "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.15}}}, "B": {"code": "B", "label": "점진적 감축", "effect": {"scores": {"defense": 0, "economy": 0, "environment": 20, "publicSentiment": 0}, "weights": {"alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0, "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0, "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0, "militarySecurity": 0.0, "pollutionDisaster": 0.8, "resourceManagement": 0.6, "climateChangeEnergy": 0.0}}}}',
           '{"url": "https://news.example/env-1", "title": "RE0 기업 확대"}',
           '{"conditions": [{"category": "climateChangeEnergy", "operator": "MORE_THAN", "threshold": 0.1}]}'
       );

-- 5: 위성 통신망 투자 (NPC: 국방부 장관=3)
INSERT INTO public.scenario
(created_at, id, npc_id, updated_at, title, content, choices, related_article, spawn)
VALUES (
           now(),
           5,
           3,
           now(),
           '위성 통신망 투자',
           '재난 대비 위성 기반 통신망 투자 여부를 결정합니다.',
           '{"A": {"code": "A", "label": "대규모 초기 투자", "effect": {"scores": {"defense": 3, "economy": -10, "environment": 0, "publicSentiment": 100}, "weights": {"alliances": 0.0, "cyberSpace": 0.12, "biodiversity": 0.0, "fiscalPolicy": 0.0, "macroeconomy": 0.0, "publicSafety": 0.1, "socialIssues": 0.0, "healthWelfare": 0.0, "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0, "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0}}}, "B": {"code": "B", "label": "파일럿 후 단계적 확대", "effect": {"scores": {"defense": 10, "economy": 0, "environment": 0, "publicSentiment": 0}, "weights": {"alliances": 0.0, "cyberSpace": 0.6, "biodiversity": 0.0, "fiscalPolicy": 0.0, "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0, "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.6, "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0}}}}',
           '{"url": "https://news.example/space-1", "title": "재난망 테스트 결과"}',
           '{"conditions": [{"category": "publicSafety", "operator": "LESS_THAN", "threshold": 0.6}]}'
       );

-- 6: 최저임금 조정 논의 (NPC: 노동단체 대표=5)
INSERT INTO public.scenario
(created_at, id, npc_id, updated_at, title, content, choices, related_article, spawn)
VALUES (
           now(),
           6,
           5,
           now(),
           '최저임금 조정 논의',
           '물가·고용 상황을 고려해 최저임금 조정을 검토합니다.',
           '{"A": {"code": "A", "label": "인상 폭 확대", "effect": {"scores": {"defense": 0, "economy": -10, "environment": 0, "publicSentiment": 3}, "weights": {"alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0, "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0, "publicOpinion": 0.1, "protestsStrikes": 0.1, "financialMarkets": 0.0, "industryBusiness": 0.0, "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0}}}, "B": {"code": "B", "label": "점진적 인상", "effect": {"scores": {"defense": 0, "economy": 0, "environment": 0, "publicSentiment": 10}, "weights": {"alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0, "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0, "publicOpinion": 0.5, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.6, "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0}}}}',
           '{"url": "https://news.example/labor-1", "title": "노사정 위원회 재가동"}',
           '{"conditions": [{"category": "macroeconomy", "operator": "LESS_THAN", "threshold": 0.7}]}'
       );

-- 7: 필수의료 인력 확충 (NPC: 대변인=1)
INSERT INTO public.scenario
(created_at, id, npc_id, updated_at, title, content, choices, related_article, spawn)
VALUES (
           now(),
           7,
           1,
           now(),
           '필수의료 인력 확충',
           '지방 필수의료 공백 해소를 위해 인력 확충을 추진합니다.',
           '{"A": {"code": "A", "label": "공공의대 신설", "effect": {"scores": {"defense": 0, "economy": -10, "environment": 0, "publicSentiment": 30}, "weights": {"alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0, "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.5, "healthWelfare": 0.15, "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0, "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0}}}, "B": {"code": "B", "label": "지역가산·인센티브", "effect": {"scores": {"defense": 0, "economy": 0, "environment": 0, "publicSentiment": 20}, "weights": {"alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0, "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.1, "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.5, "industryBusiness": 0.0, "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0}}}}',
           '{"url": "https://news.example/health-1", "title": "지역 응급 인력난 심화"}',
           '{"conditions": [{"category": "healthWelfare", "operator": "LESS_THAN", "threshold": 0.5}]}'
       );

-- 8: 반도체 클러스터 세액공제 (NPC: 재정경제부 장관=2)
INSERT INTO public.scenario
(created_at, id, npc_id, updated_at, title, content, choices, related_article, spawn)
VALUES (
           now(),
           8,
           2,
           now(),
           '반도체 클러스터 세액공제',
           '대규모 반도체 클러스터 유치 세액공제를 검토합니다.',
           '{"A": {"code": "A", "label": "세액공제 확대", "effect": {"scores": {"defense": 0, "economy": 40, "environment": -1, "publicSentiment": 0}, "weights": {"alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0, "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0, "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.6, "industryBusiness": 0.15, "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0}}}, "B": {"code": "B", "label": "엄격한 성과조건", "effect": {"scores": {"defense": 0, "economy": 20, "environment": 0, "publicSentiment": 0}, "weights": {"alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.8, "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0, "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.8, "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0}}}}',
           '{"url": "https://news.example/ind-1", "title": "수출 회복 신호"}',
           '{"conditions": [{"category": "industryBusiness", "operator": "MORE_THAN", "threshold": 0.1}]}'
       );

-- 9: 홍수 취약지 개선사업 (NPC: 환경청 청장=4)
INSERT INTO public.scenario
(created_at, id, npc_id, updated_at, title, content, choices, related_article, spawn)
VALUES (
           now(),
           9,
           4,
           now(),
           '홍수 취약지 개선사업',
           '우기 대비 하천 정비와 배수 인프라 투자를 검토합니다.',
           '{"A": {"code": "A", "label": "선제적 대규모 투자", "effect": {"scores": {"defense": 0, "economy": -10, "environment": 30, "publicSentiment": 2}, "weights": {"alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0, "macroeconomy": 0.0, "publicSafety": 0.1, "socialIssues": 0.0, "healthWelfare": 0.0, "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0, "militarySecurity": 0.0, "pollutionDisaster": 0.12, "resourceManagement": 0.6, "climateChangeEnergy": 0.0}}}, "B": {"code": "B", "label": "지자체 매칭 중심", "effect": {"scores": {"defense": 0, "economy": 0, "environment": 20, "publicSentiment": 10}, "weights": {"alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0, "macroeconomy": 0.0, "publicSafety": 0.6, "socialIssues": 0.0, "healthWelfare": 0.0, "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0, "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.6, "climateChangeEnergy": 0.0}}}}',
           '{"url": "https://news.example/safety-1", "title": "기후위기 대응 예산 확대"}',
           '{"conditions": [{"category": "pollutionDisaster", "operator": "MORE_THAN", "threshold": 0.15}]}'
       );

-- 10: 다자안보 협의체 가입 (NPC: 국방부 장관=3)
INSERT INTO public.scenario
(created_at, id, npc_id, updated_at, title, content, choices, related_article, spawn)
VALUES (
           now(),
           10,
           3,
           now(),
           '다자안보 협의체 가입',
           '새로운 다자안보 협의체 참여를 검토합니다.',
           '{"A": {"code": "A", "label": "정식 가입 추진", "effect": {"scores": {"defense": 30, "economy": 0, "environment": 0, "publicSentiment": 10}, "weights": {"alliances": 0.14, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0, "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0, "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0, "militarySecurity": 0.6, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0}}}, "B": {"code": "B", "label": "옵저버로 참여", "effect": {"scores": {"defense": 10, "economy": 0, "environment": 0, "publicSentiment": 0}, "weights": {"alliances": 0.8, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0, "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0, "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0, "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0}}}}',
           '{"url": "https://news.example/diplo-1", "title": "다자외교 복원"}',
           '{"conditions": [{"category": "alliances", "operator": "MORE_THAN", "threshold": 0.1}, {"category": "publicOpinion", "operator": "MORE_THAN", "threshold": 0.5}]}'
       );

COMMIT;
