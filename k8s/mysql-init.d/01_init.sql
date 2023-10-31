CREATE SCHEMA IF NOT EXISTS `pickup` DEFAULT CHARACTER SET utf8mb4;

create user root@'%' identified by 'Kakao@123';
GRANT ALL ON pickup.* TO 'root'@'%';
FLUSH PRIVILEGES;
