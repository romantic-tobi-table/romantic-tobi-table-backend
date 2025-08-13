-- category 컬럼이 있으면 드랍, 없으면 아무 것도 안 함
SET @col := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'store'
    AND COLUMN_NAME = 'category'
);

SET @sql := IF(@col > 0, 'ALTER TABLE store DROP COLUMN category', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
