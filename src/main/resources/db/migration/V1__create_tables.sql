-- V1__create_tables.sql

-- User Table
CREATE TABLE User (
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
CREATE TABLE User_Interest (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    interest_name VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id)
);

-- Store Table
CREATE TABLE Store (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL
);

-- Receipt Table
CREATE TABLE Receipt (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    total_price INT NOT NULL,
    recognized_text TEXT NOT NULL,
    recognized_date DATE NOT NULL,
    verified BOOLEAN NOT NULL,
    ocr_raw_json TEXT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id),
    FOREIGN KEY (store_id) REFERENCES Store(id),
    UNIQUE (user_id, store_id, recognized_date)
);

-- PointTransaction Table
CREATE TABLE Point_Transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL, -- ENUM: RECEIPT_EARN, FEED_SPEND, GREETING_REWARD, ADJUST, LEVELUP_SPEND
    amount INT NOT NULL,
    balance_after INT NOT NULL,
    ref_table VARCHAR(32),
    ref_id BIGINT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id)
);

-- Pet Table
CREATE TABLE Pet (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    level INT NOT NULL,
    current_point INT NOT NULL,
    exp INT NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id)
);



-- Review Table
CREATE TABLE Review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    rating INT NOT NULL,
    is_verified BOOLEAN NOT NULL,
    receipt_id BIGINT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id),
    FOREIGN KEY (store_id) REFERENCES Store(id),
    FOREIGN KEY (receipt_id) REFERENCES Receipt(id)
);

-- GreetingLog Table
CREATE TABLE Greeting_Log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    greeted_date DATE NOT NULL,
    point_awarded INT NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id),
    UNIQUE (user_id, greeted_date)
);

-- Achievement Table
CREATE TABLE Achievement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

-- UserAchievement Table
CREATE TABLE User_Achievement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    achievement_id BIGINT NOT NULL,
    progress INT NOT NULL,
    last_updated_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id),
    FOREIGN KEY (achievement_id) REFERENCES Achievement(id),
    UNIQUE (user_id, achievement_id)
);

