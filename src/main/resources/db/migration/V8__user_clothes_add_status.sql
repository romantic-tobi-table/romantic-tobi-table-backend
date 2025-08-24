-- V8__user_clothes_add_status.sql

-- 1) status 컬럼 추가 (문자열 ENUM 대용)
ALTER TABLE user_clothes
    ADD COLUMN status VARCHAR(16) NOT NULL DEFAULT 'OWNED';

-- 2) 기존 is_equipped 값을 status로 이관
UPDATE user_clothes
SET status = CASE WHEN is_equipped = 1 THEN 'EQUIPPED' ELSE 'OWNED' END;

-- 3) is_equipped 컬럼 제거
ALTER TABLE user_clothes
DROP COLUMN is_equipped;

-- 4) 조회 최적화(선택)
CREATE INDEX idx_user_clothes_pet_status ON user_clothes (pet_id, status);

