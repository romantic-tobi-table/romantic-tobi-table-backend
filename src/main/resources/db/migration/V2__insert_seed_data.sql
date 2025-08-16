-- V2__insert_seed_data.sql
-- 개발용 시드 데이터 (부모 -> 자식 순으로 INSERT)
-- 주의: 운영 환경에서는 사용하지 마세요.

START TRANSACTION;
SET FOREIGN_KEY_CHECKS = 0;

-- 1) USER (부모)
INSERT INTO `user`
(id, user_id, password, birthday, nickname, gender, allow_notification, created_at, deleted_at)
VALUES
 (1001, 'tom',  '$2a$10$7EqJtq98hPqEX7fNZaFWoOB1p8nG8fDeihdj5Z8SGvtQvECOH7/.', '1999-01-01', '톰',  'M', 1, NOW(), NULL),
 (1002, 'jane', '$2a$10$7EqJtq98hPqEX7fNZaFWoOB1p8nG8fDeihdj5Z8SGvtQvECOH7/.', '2000-05-20', '제인', 'F', 1, NOW(), NULL);

-- 2) STORE (부모)  <-- ★ 수정: id, name, address만
INSERT INTO `store`
(id, name, address)
VALUES
 (2001, '토미카페',   '경북 구미시 원평동 123'),
 (2002, '토미분식',   '경북 구미시 진평동 45'),
 (2003, '토미시네마', '경북 구미시 비산동 77');

-- 3) ACHIEVEMENT (부모)
INSERT INTO `achievement`
(id, code, name, description)
VALUES
 (3001, 'ATTENDANCE_SEQ', '연속 출석', '연속으로 n일 출석 시 달성'),
 (3002, 'RECEIPT',        '영수증 등록', '영수증을 등록하면 달성'),
 (3003, 'FEEDING',        '먹이 주기', '펫에게 먹이를 주면 달성');

-- 4) CLOTHES (부모)
INSERT INTO `clothes`
(id, name, category, price, image_url, description)
VALUES
    (4001, '밀짚 모자',     'HEAD', 500, 'https://example.com/clothes/head-cap.png', '챙이 적당히 넓다. 평범하지만 튼튼하다. 쓰고 뛰어도 쉽게 벗겨지지 않는다.'),
    (4002, '선글라스', 'EYE',  500, 'https://example.com/clothes/eye-sunglass.png', '누가 봐도 연예인 같다. 멋 부릴 때 쓴다.'),
    (4003, '턱수염',   'FACE', 100, 'https://example.com/clothes/face-mustache.png', '멋있는 턱수염이다. 중년미가 흐른다.'),
    (4004, '리본',   'HEAD', 500, 'https://example.com/clothes/head-ribbon.png', '엄청 풍성한 리본. 뒤로 넘어져도 안 아플 것 같다');

-- 5) PET (user FK, user_id UNIQUE)
INSERT INTO `pet`
(id, user_id, level, current_point, exp, updated_at)
VALUES
 (5001, 1001, 1, 0, 0, NOW()),
 (5002, 1002, 1, 0, 0, NOW());

-- 6) REWARD (부모)
INSERT INTO `reward`
(id, reward_name, description, cost_point, is_active, stock)
VALUES
 (6001, '커피 쿠폰',    '아메리카노 교환권',   1000, 1, 100),
 (6002, '영화 예매권',  '영화 1인 예매권',     5000, 1,  50),
 (6003, '치킨 기프티콘','교환처에서 사용 가능', 8000, 1,  30);

-- 7) GREETING_LOG (user FK, UNIQUE (user_id, greeted_date))
INSERT INTO `greeting_log`
(id, user_id, greeted_date, point_awarded, created_at)
VALUES
 (7001, 1001, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 10, NOW()),
 (7002, 1001, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 10, NOW()),
 (7003, 1002, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 10, NOW());

-- 8) RECEIPT (user/store FK, UNIQUE (user_id, store_id, recognized_date))
INSERT INTO `receipt`
(id, user_id, store_id, total_price, recognized_text, recognized_date, verified, ocr_raw_json, created_at)
VALUES
 (8001, 1001, 2001, 5800, '아메리카노 1, 쿠키 1', DATE_SUB(CURDATE(), INTERVAL 5 DAY), 1, NULL, NOW()),
 (8002, 1001, 2003, 12000, '영화 티켓 1',          DATE_SUB(CURDATE(), INTERVAL 3 DAY), 1, NULL, NOW()),
 (8003, 1002, 2001, 4300,  '라떼 1',                DATE_SUB(CURDATE(), INTERVAL 1 DAY), 0, NULL, NOW());

-- 9) REVIEW (user/store/receipt FK)
INSERT INTO `review`
(id, user_id, store_id, content, rating, is_verified, receipt_id, created_at)
VALUES
 (9001, 1001, 2001, '커피가 맛있어요!', 5, 1, 8001, NOW()),
 (9002, 1001, 2003, '영화관 좌석이 편했어요.', 4, 1, 8002, NOW()),
 (9003, 1002, 2001, '라떼가 진해요.', 4, 0, 8003, NOW());

-- 10) POINT_TRANSACTION (user FK)
INSERT INTO `point_transaction`
(id, user_id, type, amount, balance_after, ref_table, ref_id, created_at)
VALUES
 (10001, 1001, 'EARN',  580,   580,  'receipt', 8001, NOW()),
 (10002, 1001, 'EARN', 1200,  1780,  'receipt', 8002, NOW()),
 (10003, 1001, 'SPEND', -200,  1580,  'clothes', 4002, NOW()),
 (10004, 1002, 'EARN',  430,   430,  'receipt', 8003, NOW());

-- 11) USER_INTEREST (user FK)
INSERT INTO `user_interest`
(id, user_id, interest_name)
VALUES
 (11001, 1001, 'FOOD'),
 (11002, 1001, 'MOVIE'),
 (11003, 1002, 'CAFE');

-- 12) USER_ACHIEVEMENT (user/achievement FK, UNIQUE (user_id, achievement_id))
INSERT INTO `user_achievement`
(id, user_id, achievement_id, progress, last_updated_at)
VALUES
 (12001, 1001, 3001, 3, NOW()),
 (12002, 1001, 3002, 2, NOW()),
 (12003, 1002, 3002, 1, NOW());

-- 13) USER_CLOTHES (pet/clothes FK, UNIQUE (pet_id, clothes_id))
INSERT INTO `user_clothes`
(id, pet_id, clothes_id, is_equipped, purchased_at)
VALUES
 (13001, 5001, 4001, 1, NOW()),
 (13002, 5001, 4002, 0, NOW()),
 (13003, 5002, 4003, 1, NOW());

-- 14) USER_REWARD (user/reward FK)
INSERT INTO `user_reward`
(id, user_id, reward_id, used, code, issued_at, used_at)
VALUES
 (14001, 1001, 6001, 0, 'CF-1001-AX1', NOW(), NULL),
 (14002, 1001, 6002, 1, 'MV-5000-BB2', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
 (14003, 1002, 6001, 0, 'CF-1001-CC3', NOW(), NULL);

SET FOREIGN_KEY_CHECKS = 1;
COMMIT;
