-- ====================================================================
-- File: data.sql
-- Purpose: 시드 데이터 일괄 삽입
-- Note:
--   - 기존에 넣어둔 시드 데이터에 덮어쓰기
-- ====================================================================

-- --------------------------------------------------------------------
-- 시나리오 테이블 구조 변경 - MVP 3차 반영
-- --------------------------------------------------------------------

-- 컬럼 추가 (이미 존재한다면 스킵)
ALTER TABLE scenario
    ADD COLUMN IF NOT EXISTS type varchar(255) NOT NULL DEFAULT 'ORIGIN';

ALTER TABLE scenario
    ADD COLUMN IF NOT EXISTS  article_id varchar(20);

ALTER TABLE scenario
    ADD COLUMN IF NOT EXISTS  origin_scenario_id bigint;

-- 기존 FK 제약조건이 있으면 삭제
ALTER TABLE scenario
    DROP CONSTRAINT IF EXISTS fk_scenario_origin;

-- 새로운 FK 제약조건 추가
ALTER TABLE scenario
    ADD CONSTRAINT fk_scenario_origin
        FOREIGN KEY (origin_scenario_id)
            REFERENCES scenario (id);

-- 기존 type 제약조건이 있다면 먼저 제거
ALTER TABLE scenario
    DROP CONSTRAINT IF EXISTS scenario_type_check;

-- 새 type 제약조건 추가
ALTER TABLE scenario
    ADD CONSTRAINT scenario_type_check
        CHECK (type IN ('ORIGIN', 'CONSEQUENCE', 'EVENT'));


-- --------------------------------------------------------------------
-- Ending 시드 데이터
-- --------------------------------------------------------------------
INSERT INTO endings (id, code, title, content, condition, image_id, created_at, updated_at)
VALUES
    (1, 'ECO_MAX', '경제가 100이 되었습니다.', '부는 쌓였지만, 나눌 생각은 없었다.', 'economy==100', NULL, now(), now()),
    (2, 'ECO_MIN', '경제가 0이 되었습니다.', '돈도, 빵도, 희망도 사라졌다.', 'economy==0', NULL, now(), now()),
    (3, 'DEF_MAX', '국방이 100이 되었습니다.', '철옹성은 완성됐다. 감옥도 함께.', 'defense==100', NULL, now(), now()),
    (4, 'DEF_MIN', '국방이 0이 되었습니다.', '군대가 사라지자, 나라가 사라졌다.', 'defense==0', NULL, now(), now()),
    (5, 'PUB_MAX', '민심이 100이 되었습니다.', '사랑이 지나쳐 숭배가 되었다.', 'publicSentiment==100', NULL, now(), now()),
    (6, 'PUB_MIN', '민심이 0이 되었습니다.', '군중은 환호를 멈추고 돌을 던졌다.', 'publicSentiment==0', NULL, now(), now()),
    (7, 'ENV_MAX', '환경이 100이 되었습니다.', '숲은 살아났지만, 사람은 사라졌다.', 'environment==100', NULL, now(), now()),
    (8, 'ENV_MIN', '환경이 0이 되었습니다.', '강은 말랐고, 숨은 막혔다.', 'environment==0', NULL, now(), now()),
    (9,  'DOUBLE_OVER', '나라가 반이나 남았네?', '두개의 지표가 붕괴했습니다...', 'totalOver==2', NULL, now(), now()),
    (10, 'TRIPLE_OVER', '삼권붕괴', '세개의 지표가 붕괴했습니다... 우리나라의 미래는 어떻게 될까요?', 'totalOver==3', NULL, now(), now()),
    (11, 'QUAD_OVER', '(나라가) 폭싹 망했수다', '진정한 뉴토피아', 'totalOver==4', NULL, now(), now())
ON CONFLICT (id) DO UPDATE SET
                               code = EXCLUDED.code,
                               title = EXCLUDED.title,
                               content = EXCLUDED.content,
                               condition = EXCLUDED.condition,
                               image_id = EXCLUDED.image_id,
                               updated_at = now();


-- --------------------------------------------------------------------
-- NPC 시드 데이터
-- --------------------------------------------------------------------
INSERT INTO npc (id, code, name, tags, image_id, created_at, updated_at)
VALUES
    (1,  'ECO_1', '과학자',         '["경제","과학"]'::jsonb,         NULL, now(), now()),
    (2,  'ECO_2', '회사원',         '["경제","직장인"]'::jsonb,       NULL, now(), now()),
    (3,  'ECO_3', '노동자',         '["경제","노동"]'::jsonb,         NULL, now(), now()),
    (4,  'ECO_4', '재정경제부 장관',  '["경제","정부","장관"]'::jsonb,   NULL, now(), now()),
    (5,  'DEF_1', '국방부 장관',     '["국방","정부","장관"]'::jsonb,  NULL, now(), now()),
    (6,  'DEF_2', '국가요원',       '["국방","요원"]'::jsonb,         NULL, now(), now()),
    (7,  'ENV_1', '농사꾼',         '["환경","농업"]'::jsonb,         NULL, now(), now()),
    (8,  'ENV_2', '개구리',         '["환경","동물"]'::jsonb,         NULL, now(), now()),
    (9,  'ENV_3', '환경청 청장',     '["환경","정부","청장"]'::jsonb,   NULL, now(), now()),
    (10, 'PUB_1', '여학생',         '["민심","학생"]'::jsonb,         NULL, now(), now()),
    (11, 'PUB_2', '남학생',         '["민심","학생"]'::jsonb,         NULL, now(), now()),
    (12, 'PUB_3', '아저씨',         '["민심","시민"]'::jsonb,         NULL, now(), now()),
    (13, 'PUB_4', '주부',           '["민심","가정"]'::jsonb,         NULL, now(), now()),
    (14, 'PUB_5', '어린아이',        '["민심","아동"]'::jsonb,         NULL, now(), now()),
    (15, 'PUB_6', '노동단체 대표',    '["민심","노동","대표"]'::jsonb,   NULL, now(), now()),
    (16, 'ANY_1', '기자',           '["언론","미디어"]'::jsonb,       NULL, now(), now()),
    (17, 'ANY_2', '의사',           '["의료","보건"]'::jsonb,         NULL, now(), now()),
    (18, 'ANY_3', '종교인',         '["종교"]'::jsonb,                NULL, now(), now()),
    (19, 'ANY_4', '취준생',         '["취업","학생"]'::jsonb,          NULL, now(), now()),
    (20, 'ANY_5', '대변인',         '["언론","외교","정부"]'::jsonb,    NULL, now(), now())
ON CONFLICT (id) DO UPDATE SET
                               code = EXCLUDED.code,
                               name = EXCLUDED.name,
                               tags = EXCLUDED.tags,
                               image_id = EXCLUDED.image_id,
                               updated_at = now();


-- --------------------------------------------------------------------
-- 이벤트 NPC 시드 데이터
-- --------------------------------------------------------------------
INSERT INTO npc (id, code, name, tags, image_id, created_at, updated_at)
VALUES
    (1001, 'EVENT_1', '예렌 예거',   '["이벤트","민심","국방"]'::jsonb,             NULL, now(), now()),
    (1003, 'EVENT_3', '자르반74세',  '["이벤트","민심"]'::jsonb,                    NULL, now(), now()),
    (1004, 'EVENT_4', '공항도둑',    '["이벤트","국방"]'::jsonb,                    NULL, now(), now()),
    (1005, 'EVENT_5', '리코더빌런',  '["이벤트","민심"]'::jsonb,                    NULL, now(), now()),
    (1006, 'EVENT_6', '게임디렉터',  '["이벤트","경제","국방","환경","민심"]'::jsonb, NULL, now(), now()),
    (1007, 'EVENT_7', '엘사',       '["이벤트","경제","환경"]'::jsonb,             NULL, now(), now())
ON CONFLICT (id) DO UPDATE SET
                               code = EXCLUDED.code,
                               name = EXCLUDED.name,
                               tags = EXCLUDED.tags,
                               image_id = EXCLUDED.image_id,
                               updated_at = now();


-- --------------------------------------------------------------------
-- 이벤트 시나리오 시드 데이터
-- --------------------------------------------------------------------
INSERT INTO scenario (
    id,
    npc_id,
    type,
    title,
    content,
    choices,
    related_article,
    origin_scenario_id,
    spawn,
    created_at,
    updated_at
)
VALUES
-- Event_1: 예렌 예거
(1, 1001, 'EVENT',
 '예렌 예거의 제안',
 '나와 함께 우리 나라의 적들을 구축하지 않겠나?',
 '{
   "A": {
     "code": "A",
     "label": "구축한다.",
     "comments": ["그래! 드디어 우리가 반격할 때가 왔다!", "두렵지만… 우리의 자유를 위해 싸우겠어!", "시장님과 함께라면 승리할 수 있다!"],
     "effect": {
       "scores": {
         "applyType": "RELATIVE",
         "economy": 0,
         "defense": 30,
         "publicSentiment": 30,
         "environment": 0
       },
       "weights": {
         "applyType": "RELATIVE",
         "alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0,
         "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0,
         "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0,
         "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0
       }
     }
   },
   "B": {
     "code": "B",
     "label": "구축하지 않는다.",
     "comments": ["겁쟁이냐? 저 적들을 두고 본단 말이냐!", "전쟁은 결국 우리 모두를 파멸시킬 뿐이야.", "평화를 원한다면 피를 흘리지 않는 길도 필요하다."],
     "effect": {
       "scores": {
         "applyType": "RELATIVE",
         "economy": 0,
         "defense": -30,
         "publicSentiment": -30,
         "environment": 0
       },
       "weights": {
         "applyType": "RELATIVE",
         "alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0,
         "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0,
         "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0,
         "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0
       }
     }
   }
 }'::jsonb,
 '{
    "title": "예렌 예거, ‘적 구축’ 발언으로 시민들 술렁",
    "url": null,
    "content": "최근 시청 광장에 나타난 예렌 예거는 “우리 나라의 적들을 구축하지 않겠나?”라며 강력한 메시지를 던졌다. 그의 발언은 일부 시민들 사이에서 열렬한 환호를 얻었지만, 또 다른 시민들은 지나치게 과격한 태도에 우려를 표했다. 전문가들은 “예거의 언행이 민심을 크게 흔들 수 있다”며 정치적 파장을 주시하고 있다."
 }'::jsonb,
 null, '{"conditions":[]}'::jsonb, now(), now()),

-- Event_3: 자르반74세
(3, 1003, 'EVENT',
 '자르반74세의 결의',
 '내 의지로, 여기서 끝을 보겠노라!',
 '{
   "A": {
     "code": "A",
     "label": "자르반 74세와 손을 잡는다.",
     "comments": ["시장님 미쳤어요? 저 빌런이랑 손을 잡는다고요?ㅋㅋ", "근데 좀 멋있긴 하다… 뭔가 간지 폭발이네.", "아무리 나라가 상황이 안좋아도 이게 맞아요??;;"],
     "effect": {
       "scores": {
         "applyType": "RELATIVE",
         "economy": 0,
         "defense": 0,
         "publicSentiment": -30,
         "environment": 0
       },
       "weights": {
         "applyType": "RELATIVE",
         "alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0,
         "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0,
         "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0,
         "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0
       }
     }
   },
   "B": {
     "code": "B",
     "label": "지하철 안전 요원을 호출한다.",
     "comments": ["역시 우리 시장님! 안전요원 호출은 국룰이지 ㅋㅋ", "지하철 빌런은 안전요원에겐 답도 없지!", "와… 이거 바로 레전드 사건 기록각ㅋ"],
     "effect": {
       "scores": {
         "applyType": "RELATIVE",
         "economy": 0,
         "defense": 0,
         "publicSentiment": 30,
         "environment": 0
       },
       "weights": {
         "applyType": "RELATIVE",
         "alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0,
         "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0,
         "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0,
         "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0
       }
     }
   }
 }'::jsonb,
 '{
   "title": "자르반 74세, 지하철서 ‘끝을 보겠다’ 선언… 승객들 충격",
   "url": null,
   "content": "오늘 아침 출근길 지하철에서 ‘자르반 74세’로 알려진 한 인물이 등장, “내 의지로 여기서 끝을 보겠다”고 외치며 주변을 긴장시켰다. 일부 시민들은 그의 카리스마 넘치는 모습에 놀라움을 표했으나, 다수의 승객은 공포와 혼란을 겪었다. 사건은 안전 요원들의 신속한 대응으로 마무리됐으며, 현재 경찰은 자르반 74세의 신원과 의도를 조사 중이다."
 }'::jsonb,
 null, '{"conditions":[]}'::jsonb, now(), now()),

-- Event_4: 공항도둑
(4, 1004, 'EVENT',
 '공항도둑의 난동',
 '왜 나한테만 그러는데에!!@#$%#@$%@%@#$(이해할 수 없는 말을 반복한다…)',
 '{
   "A": {
     "code": "A",
     "label": "@#$%$#@@%$#(맞대응한다!)",
     "comments": ["아… 우리 시장님도 갔다…", "와 이거 레알 광인 대 광인 싸움ㅋㅋ", "시장님이 저 미친 톤에 맞대응한다고??ㅋㅋㅋㅋ"],
     "effect": {
       "scores": {
         "applyType": "RELATIVE",
         "economy": 0,
         "defense": -30,
         "publicSentiment": 0,
         "environment": 0
       },
       "weights": {
         "applyType": "RELATIVE",
         "alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0,
         "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0,
         "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0,
         "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0
       }
     }
   },
   "B": {
     "code": "B",
     "label": "경찰에 신고한다.",
     "comments": ["역시 정상인은 경찰을 부르지 ㅋㅋ 시장님 굿잡!", "이제 안심하고 공항갈 수 있겠다~", "공권력을 저렇데 쓰는것도 웃기다ㅋㅋ"],
     "effect": {
       "scores": {
         "applyType": "RELATIVE",
         "economy": 0,
         "defense": 30,
         "publicSentiment": 0,
         "environment": 0
       },
       "weights": {
         "applyType": "RELATIVE",
         "alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0,
         "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0,
         "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0,
         "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0
       }
     }
   }
 }'::jsonb,
 '{
   "title": "공항 도둑, 이해 불가 발언 반복… 시민 불안 고조",
   "url": null,
   "content": "인천국제공항에서 ‘공항 도둑’으로 불리는 괴한이 이해할 수 없는 괴성을 지르며 난동을 부려 시민들이 큰 혼란에 빠졌다. 목격자들은 “계속해서 알아들을 수 없는 말을 반복했다”고 증언했다. 공항 당국은 즉시 경찰을 투입하여 상황을 진압했으며, 전문가들은 해당 인물이 정신적 문제를 안고 있을 가능성을 제기했다."
 }'::jsonb,
 null, '{"conditions":[]}'::jsonb, now(), now()),

-- Event_5: 리코더빌런
(5, 1005, 'EVENT',
 '리코더빌런의 습격',
 '(말 없이 리코더를 휘두른다.)',
 '{
   "A": {
     "code": "A",
     "label": "피한다.",
     "comments": ["이상한 사람은 피하는게 상책이긴 해ㅇㅇ", "시장님… 저게 최선이었나요?", "리코더에 밀려난 우리 시장님… 전설의 굴욕 장면 탄생ㅋㅋ"],
     "effect": {
       "scores": {
         "applyType": "RELATIVE",
         "economy": 0,
         "defense": 0,
         "publicSentiment": -30,
         "environment": 0
       },
       "weights": {
         "applyType": "RELATIVE",
         "alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0,
         "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0,
         "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0,
         "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0
       }
     }
   },
   "B": {
     "code": "B",
     "label": "경호원을 부른다.",
     "comments": ["경호원 vs 리코더빌런 ㅋㅋ 이거 영상 뜨면 유튜브 100만각임.", "와 시장님 드디어 프로페셔널한 판단 나왔네!", "리코더 뺏기면 이제 뭐 단소라도 휘두르나?"],
     "effect": {
       "scores": {
         "applyType": "RELATIVE",
         "economy": 0,
         "defense": 0,
         "publicSentiment": 30,
         "environment": 0
       },
       "weights": {
         "applyType": "RELATIVE",
         "alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0,
         "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0,
         "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0,
         "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0
       }
     }
   }
 }'::jsonb,
 '{
   "title": "도심 한복판서 리코더 휘두른 ‘리코더 빌런’ 등장",
   "url": null,
   "content": "서울 시내 번화가에서 한 남성이 말없이 리코더를 휘두르며 시민들에게 위협을 가하는 사건이 발생했다. 시민들은 당황스러운 상황에 혼란을 겪었고, 일부는 휴대폰으로 촬영해 SNS에 공유했다. 영상은 빠르게 확산되며 ‘리코더 빌런’이라는 별칭이 붙었다. 경찰은 해당 인물을 현장에서 제압하고 추가 범행 여부를 조사 중이다."
 }'::jsonb,
 null, '{"conditions":[]}'::jsonb, now(), now()),

-- Event_6: 게임디렉터
(6, 1006, 'EVENT',
 '게임디렉터의 제안',
 '정상화 해드릴까요?',
 '{
   "A": {
     "code": "A",
     "label": "나가주세요.",
     "comments": ["와 시장님 ㄹㅇ 직설적ㅋㅋㅋㅋ", "게임디렉터 화나서 패치노트에 시장 이름 박히는 거 아님?ㅋㅋ", "그래, 우리도 저 사람 좀 나갔으면 했어…"],
     "effect": {
       "scores": {
         "applyType": "RELATIVE",
         "economy": 0,
         "defense": 0,
         "publicSentiment": 0,
         "environment": 0
       },
       "weights": {
         "applyType": "RELATIVE",
         "alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0,
         "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0,
         "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0,
         "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0
       }
     }
   },
   "B": {
     "code": "B",
     "label": "부탁드립니다.",
     "comments": ["ㅋㅋㅋ 시장님도 결국 의존해버렸네", "그는 신이야...", "정상화해줬잖아!!"],
     "effect": {
       "scores": {
         "applyType": "ABSOLUTE",
         "economy": 50,
         "defense": 50,
         "publicSentiment": 50,
         "environment": 50
       },
       "weights": {
         "applyType": "RELATIVE",
         "alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0,
         "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0,
         "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0,
         "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0
       }
     }
   }
 }'::jsonb,
 '{
   "title": "게임 디렉터, ‘정상화 해드릴까요?’ 발언 화제",
   "url": null,
   "content": "국내 유명 게임 디렉터가 시민들에게 “정상화 해드릴까요?”라는 의미심장한 발언을 남겨 화제가 되고 있다. 일각에서는 실제 게임 운영과 관련된 이야기일 수 있다는 해석이 나오고 있으며, 다른 쪽에서는 그를 ‘신적인 존재’에 비유하며 숭배하는 팬덤 현상까지 나타나고 있다. 전문가들은 “현실과 가상의 경계가 흐려지는 현상의 한 단면”이라고 분석했다."
 }'::jsonb,
 null, '{"conditions":[]}'::jsonb, now(), now()),

-- Event_7: 엘사
(7, 1007, 'EVENT',
 '엘사의 요구',
 '나 지금 굉장히 습하고 더우니까 잔말 말고 파워냉방으로 틀어.',
 '{
   "A": {
     "code": "A",
     "label": "파워냉방으로 틀어준다.",
     "comments": ["시장님… 전기요금 폭탄 각인데요?ㅋㅋ", "엘사 기분은 좋아졌는데, 우리 지갑은 얼어붙음ㅠㅠ", "와 시민들 얼음왕국 체험 중ㅋㅋ"],
     "effect": {
       "scores": {
         "applyType": "RELATIVE",
         "economy": -30,
         "defense": 0,
         "publicSentiment": 0,
         "environment": -50
       },
       "weights": {
         "applyType": "RELATIVE",
         "alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0,
         "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0,
         "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0,
         "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0
       }
     }
   },
   "B": {
     "code": "B",
     "label": "현행법상 냉방 제한 온도 기준에 맞춘다.",
     "comments": ["시장님 현실적 판단 굿… 근데 좀 더워요ㅠ", "엘사 화나서 눈보라 치는 거 아님?ㅋㅋ", "여름철 온도는 26~28도가 적절하지~"],
     "effect": {
       "scores": {
         "applyType": "RELATIVE",
         "economy": 30,
         "defense": 0,
         "publicSentiment": 0,
         "environment": 50
       },
       "weights": {
         "applyType": "RELATIVE",
         "alliances": 0.0, "cyberSpace": 0.0, "biodiversity": 0.0, "fiscalPolicy": 0.0,
         "macroeconomy": 0.0, "publicSafety": 0.0, "socialIssues": 0.0, "healthWelfare": 0.0,
         "publicOpinion": 0.0, "protestsStrikes": 0.0, "financialMarkets": 0.0, "industryBusiness": 0.0,
         "militarySecurity": 0.0, "pollutionDisaster": 0.0, "resourceManagement": 0.0, "climateChangeEnergy": 0.0
       }
     }
   }
 }'::jsonb,
 '{
   "title": "엘사, ‘파워냉방 틀어라’ 요구... 여름철 냉방 논란 불붙어",
   "url": null,
   "content": "폭염 속 한 시청 건물에서 ‘엘사’라 불리는 여성이 “무조건 파워냉방으로 틀어라”라고 강하게 요구한 사건이 시민들 사이에서 화제가 되고 있다. 이로 인해 전기요금과 에너지 소비 문제가 다시 한번 도마에 올랐다. 환경단체는 “과도한 냉방은 기후 위기를 악화시킨다”고 지적했으며, 일부 시민들은 “그래도 덕분에 시원했다”며 상반된 반응을 보였다."
 }'::jsonb,
 null, '{"conditions":[]}'::jsonb, now(), now())
ON CONFLICT (id) DO UPDATE SET
                               npc_id             = EXCLUDED.npc_id,
                               type               = EXCLUDED.type,
                               title              = EXCLUDED.title,
                               content            = EXCLUDED.content,
                               choices            = EXCLUDED.choices,
                               related_article    = EXCLUDED.related_article,
                               origin_scenario_id = EXCLUDED.origin_scenario_id,
                               spawn              = EXCLUDED.spawn,
                               updated_at         = now();
