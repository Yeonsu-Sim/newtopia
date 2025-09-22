-- ====================================================================
-- File: data.sql
-- Purpose: 시드 데이터 일괄 삽입
-- Note:
--   - 기존에 넣어둔 시드 데이터에 덮어쓰기
-- ====================================================================


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
