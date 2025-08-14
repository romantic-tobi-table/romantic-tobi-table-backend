-- V8__standardize_table_names.sql
-- 이 마이그레이션은 대소문자가 혼용된 테이블 이름을 snake_case로 표준화합니다.
-- AWS(Linux) 환경에서 발생하는 테이블명 대소문자 구분 문제를 해결합니다.

-- 1. Clothes 테이블 이름 표준화
RENAME TABLE `Clothes` TO `clothes`;

-- 2. GreetingLog 테이블 이름 표준화 및 중복 제거
RENAME TABLE `GreetingLog` TO `greeting_log`;
DROP TABLE IF EXISTS `Greeting_Log`;

-- 3. PointTransaction 테이블 이름 표준화 및 중복 제거
RENAME TABLE `PointTransaction` TO `point_transaction`;
DROP TABLE IF EXISTS `Point_Transaction`;

-- 4. UserClothes 테이블 이름 표준화 및 중복 제거
RENAME TABLE `UserClothes` TO `user_clothes`;
DROP TABLE IF EXISTS `User_Clothes`;

-- 5. UserAchievement 테이블 이름 표준화 및 중복 제거
RENAME TABLE `UserAchievement` TO `user_achievement`;
DROP TABLE IF EXISTS `User_Achievement`;

-- 6. UserReward 테이블 이름 표준화 및 중복 제거
RENAME TABLE `UserReward` TO `user_reward`;
DROP TABLE IF EXISTS `User_Reward`;

-- 7. UserInterest 테이블 이름 표준화 및 중복 제거
RENAME TABLE `UserInterest` TO `user_interest`;
DROP TABLE IF EXISTS `User_Interest`;

-- 8. UserProgress 테이블 이름 표준화 및 중복 제거
RENAME TABLE `UserProgress` TO `user_progress`;
DROP TABLE IF EXISTS `userprogress`;

-- 9. Achievement 테이블 이름 표준화
RENAME TABLE `Achievement` TO `achievement`;

-- 10. AchievementMilestone 테이블 이름 표준화
RENAME TABLE `AchievementMilestone` TO `achievement_milestone`;

-- 외래 키 제약 조건이 있는 다른 테이블들도 위와 같은 방식으로 처리해야 할 수 있습니다.
-- 이 스크립트는 현재 파악된 테이블들을 기준으로 작성되었습니다.
