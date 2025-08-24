INSERT INTO achievement_milestone (achievement_id, milestone_value, name, description, type)
VALUES
    -- Attendance Milestones (achievement_id = 3001 for ATTENDANCE_SEQ)
    (3001, 3, '3일 연속 출석', '3일 연속 출석 달성', 'ATTENDANCE'),
    (3001, 7, '7일 연속 출석', '7일 연속 출석 달성', 'ATTENDANCE'),
    (3001, 15, '15일 연속 출석', '15일 연속 출석 달성', 'ATTENDANCE'),
    (3001, 30, '30일 연속 출석', '30일 연속 출석 달성', 'ATTENDANCE'),

    -- Feeding Milestones (achievement_id = 3003 for FEEDING)
    (3003, 5, '밥 주기 5번', '토미에게 밥 5번 주기 달성', 'FEEDING'),
    (3003, 10, '밥 주기 10번', '토미에게 밥 10번 주기 달성', 'FEEDING'),
    (3003, 15, '밥 주기 15번', '토미에게 밥 15번 주기 달성', 'FEEDING'),

    -- Receipt Milestones (achievement_id = 3002 for RECEIPT)
    (3002, 5, '영수증 인증 5번', '영수증 5번 인증 달성', 'RECEIPT'),
    (3002, 10, '영수증 인증 10번', '영수증 10번 인증 달성', 'RECEIPT'),
    (3002, 15, '영수증 인증 15번', '영수증 15번 인증 달성', 'RECEIPT');