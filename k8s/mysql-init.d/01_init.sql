CREATE SCHEMA IF NOT EXISTS `pickup` DEFAULT CHARACTER SET utf8mb4;

GRANT ALL ON *.* TO 'root'@'localhost' WITH GRANT OPTION;
GRANT ALL ON pickup.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
