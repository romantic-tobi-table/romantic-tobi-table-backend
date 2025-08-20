-- V7__implement_user_withdrawal.sql

-- 1. Create user_withdrawal table
CREATE TABLE user_withdrawal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    reason VARCHAR(255),
    detail TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Drop the deleted_at column from the user table
ALTER TABLE user DROP COLUMN deleted_at;

-- 3. Add ON DELETE CASCADE to foreign keys referencing user table

-- Table: user_interest
ALTER TABLE user_interest DROP FOREIGN KEY user_interest_ibfk_1;
ALTER TABLE user_interest ADD CONSTRAINT fk_user_interest_user
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- Table: receipt
ALTER TABLE receipt DROP FOREIGN KEY receipt_ibfk_1;
ALTER TABLE receipt ADD CONSTRAINT fk_receipt_user
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- Table: point_transaction
ALTER TABLE point_transaction DROP FOREIGN KEY point_transaction_ibfk_1;
ALTER TABLE point_transaction ADD CONSTRAINT fk_point_transaction_user
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- Table: pet
ALTER TABLE pet DROP FOREIGN KEY pet_ibfk_1;
ALTER TABLE pet ADD CONSTRAINT fk_pet_user
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- Table: review
ALTER TABLE review DROP FOREIGN KEY review_ibfk_1;
ALTER TABLE review ADD CONSTRAINT fk_review_user
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- Table: greeting_log
ALTER TABLE greeting_log DROP FOREIGN KEY greeting_log_ibfk_1;
ALTER TABLE greeting_log ADD CONSTRAINT fk_greeting_log_user
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- Table: user_achievement
ALTER TABLE user_achievement DROP FOREIGN KEY user_achievement_ibfk_1;
ALTER TABLE user_achievement ADD CONSTRAINT fk_user_achievement_user
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- Table: user_reward
ALTER TABLE user_reward DROP FOREIGN KEY user_reward_ibfk_1;
ALTER TABLE user_reward ADD CONSTRAINT fk_user_reward_user
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- Table: user_progress (from V5)
ALTER TABLE user_progress DROP FOREIGN KEY user_progress_ibfk_1;
ALTER TABLE user_progress ADD CONSTRAINT fk_user_progress_user
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- Table: user_achievement_status (from V5)
ALTER TABLE user_achievement_status DROP FOREIGN KEY user_achievement_status_ibfk_1;
ALTER TABLE user_achievement_status ADD CONSTRAINT fk_user_achievement_status_user
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;