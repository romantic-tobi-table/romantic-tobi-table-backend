-- V5__create_achievement_tables.sql

-- Table: UserProgress
CREATE TABLE user_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    attendance_sequence INT NOT NULL DEFAULT 0,
    last_attended_at DATE,
    feeding_count INT NOT NULL DEFAULT 0,
    receipt_count INT NOT NULL DEFAULT 0,
    last_updated DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Table: AchievementMilestone
CREATE TABLE achievement_milestone (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    achievement_id BIGINT NOT NULL,
    milestone_value INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(255) NOT NULL,
    FOREIGN KEY (achievement_id) REFERENCES achievement(id)
);

-- Table: UserAchievementStatus
CREATE TABLE user_achievement_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    achievement_milestone_id BIGINT NOT NULL,
    is_achieved BOOLEAN NOT NULL DEFAULT FALSE,
    achieved_at DATETIME,
    UNIQUE (user_id, achievement_milestone_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (achievement_milestone_id) REFERENCES achievement_milestone(id)
);