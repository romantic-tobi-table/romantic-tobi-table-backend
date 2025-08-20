-- V1__create_tables.sql

-- User Table
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL,
    nickname VARCHAR(255) NOT NULL,
    gender VARCHAR(50) NOT NULL,
    allow_notification BOOLEAN NOT NULL,
    created_at DATETIME NOT NULL,
    deleted_at DATETIME
);

-- UserInterest Table
CREATE TABLE user_interest (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    interest_name VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Store Table
CREATE TABLE store (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL
);

-- Receipt Table
CREATE TABLE receipt (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    store_id BIGINT, -- Made nullable
    store_name VARCHAR(255), -- Added store_name, made nullable to match ReceiptService logic
    total_price INT NOT NULL,
    recognized_text TEXT NOT NULL,
    recognized_date DATE, -- Made nullable
    address VARCHAR(255), -- Already nullable
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (store_id) REFERENCES store(id),
    UNIQUE (user_id, store_id, recognized_date)
);

-- PointTransaction Table
CREATE TABLE point_transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL, -- ENUM: RECEIPT_EARN, FEED_SPEND, CLOTHES_BUY, REWARD_REDEEM, GREETING_REWARD, ADJUST, LEVELUP_SPEND
    amount INT NOT NULL,
    balance_after INT NOT NULL,
    ref_table VARCHAR(32),
    ref_id BIGINT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Pet Table
CREATE TABLE pet (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    level INT NOT NULL,
    current_point INT NOT NULL,
    exp INT NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Clothes Table
CREATE TABLE clothes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL
);

-- UserClothes Table
CREATE TABLE user_clothes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    clothes_id BIGINT NOT NULL,
    is_equipped BOOLEAN NOT NULL,
    purchased_at DATETIME NOT NULL,
    FOREIGN KEY (pet_id) REFERENCES pet(id),
    FOREIGN KEY (clothes_id) REFERENCES clothes(id),
    UNIQUE (pet_id, clothes_id)
);

-- Review Table
CREATE TABLE review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    rating INT NOT NULL,
    is_verified BOOLEAN NOT NULL,
    receipt_id BIGINT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (store_id) REFERENCES store(id),
    FOREIGN KEY (receipt_id) REFERENCES receipt(id)
);

-- GreetingLog Table
CREATE TABLE greeting_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    greeted_date DATE NOT NULL,
    point_awarded INT NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id),
    UNIQUE (user_id, greeted_date)
);

-- Achievement Table
CREATE TABLE achievement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

-- UserAchievement Table
CREATE TABLE user_achievement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    achievement_id BIGINT NOT NULL,
    progress INT NOT NULL,
    last_updated_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (achievement_id) REFERENCES achievement(id),
    UNIQUE (user_id, achievement_id)
);

-- Reward Table
CREATE TABLE reward (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reward_name VARCHAR(255) NOT NULL,
    description TEXT,
    cost_point INT NOT NULL,
    is_active BOOLEAN NOT NULL,
    stock INT
);

-- UserReward Table
CREATE TABLE user_reward (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    reward_id BIGINT NOT NULL,
    used BOOLEAN NOT NULL,
    code VARCHAR(128),
    issued_at DATETIME NOT NULL,
    used_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (reward_id) REFERENCES reward(id)
);