-- V5__insert_achievement_milestones.sql

INSERT INTO achievement_milestone (achievement_id, milestone_value, name, description, type)
VALUES
    -- Attendance Milestones (achievement_id = 3001 for ATTENDANCE_SEQ)
    (3001, 3, '연속 3일 출석 성공', '3일 연속 출석 달성', 'ATTENDANCE'),
    (3001, 7, '연속 7일 출석 성공', '7일 연속 출석 달성', 'ATTENDANCE'),
    (3001, 15, '연속 15일 출석 성공', '15일 연속 출석 달성', 'ATTENDANCE'),
    (3001, 30, '연속 30일 출석 성공', '30일 연속 출석 달성', 'ATTENDANCE'),

    -- Feeding Milestones (achievement_id = 3003 for FEEDING)
    (3003, 5, '토미 밥 5번 주기 성공', '토미에게 밥 5번 주기 달성', 'FEEDING'),
    (3003, 10, '토미 밥 10번 주기 성공', '토미에게 밥 10번 주기 달성', 'FEEDING'),
    (3003, 15, '토미 밥 15번 주기 성공', '토미에게 밥 15번 주기 달성', 'FEEDING'),

    -- Receipt Milestones (achievement_id = 3002 for RECEIPT)
    (3002, 5, '영수증 인증 5번 성공', '영수증 5번 인증 달성', 'RECEIPT'),
    (3002, 10, '영수증 인증 10번 성공', '영수증 10번 인증 달성', 'RECEIPT'),
    (3002, 15, '영수증 인증 15번 성공', '영수증 15번 인증 달성', 'RECEIPT');