
SET foreign_key_checks = 0;

truncate table user_tb;
truncate table board_tb;
truncate table store_tb;
truncate table beverage_tb;
truncate table match_tb;

SET foreign_key_checks = 1;

INSERT INTO user_tb (`uid`,`pwd`,`nickname`, `phone_number`, `name`, `bank`, `account`, `social_id`) VALUES ('honggildong', '{bcrypt}$2a$10$8H0OT8wgtALJkig6fmypi.Y7jzI5Y7W9PGgRKqnVeS2cLWGifwHF2', 'pickupmaster', '010-0000-0000','홍길동', '농협', '000-0000-0000-000', '1111');

INSERT INTO user_tb (`uid`,`pwd`,`nickname`, `phone_number`, `name`, `bank`, `account`, `social_id`) VALUES ('honggildong2', '{bcrypt}$2a$10$8H0OT8wgtALJkig6fmypi.Y7jzI5Y7W9PGgRKqnVeS2cLWGifwHF2', 'pickupmaster1', '010-0000-1234','홍길동', '농협', '000-0000-0000-000', '2222');

INSERT INTO user_tb (`uid`,`pwd`,`nickname`, `phone_number`, `name`, `bank`, `account`, `social_id`, `role`,`url`) VALUES ('honggildong3', 'pwd','user', '010-0000-1234','홍길동', '농협', '000-0000-0000-000', '3333', '1','https://aws.amazon.com/');

INSERT INTO user_tb (`uid`,`pwd`,`nickname`, `phone_number`, `name`, `bank`, `account`, `social_id`, `role`) VALUES ('honggildong4', 'pwd','user', '010-0000-1234','홍길동', '농협', '000-0000-0000-000', '4444', '1');

INSERT INTO user_tb (`uid`,`pwd`,`nickname`, `phone_number`, `name`, `bank`, `account`, `social_id`, `role`,`url`) VALUES ('honggildong5', 'pwd','student', '010-0000-1234','홍길동', '농협', '000-0000-0000-000', '5555', '2','https://aws.amazon.com/');

INSERT INTO user_tb (`uid`,`pwd`,`nickname`, `phone_number`, `name`, `bank`, `account`, `social_id`, `role`) VALUES ('honggildong6', 'pwd','amin', '010-0000-1234','홍길동', '농협', '000-0000-0000-000', '6666', '0');

insert into store_tb(`id`,`name`) values ('1', '전남대 후문 스타벅스');

insert into store_tb(`name`) values ('starbucks');

insert into board_tb(`tip`, `created_at`, `finished_at`, `is_match`, `destination`, `request`, `user_id`, `store_id`) values ('1000', '2023-09-30 00:14', '2023-09-30 02:24', 'N', '전남대 공대7 217호관', '빨리 와주세요', '1', '1');

insert into board_tb(`tip`, `created_at`, `finished_at`, `is_match`, `destination`, `request`, `user_id`, `store_id`) values ('1000', '2023-09-30 00:14', '2023-09-30 02:24', 'N', '전남대 공대7 217호관', '빨리 와주세요', '1', '1');

insert into board_tb(`tip`, `created_at`, `finished_at`, `is_match`, `destination`, `request`, `user_id`, `store_id`) values ('1000', '2023-09-30 00:14', '2023-09-30 02:24', 'N', '전남대 공대7 217호관', '빨리 와주세요', '1', '1');

insert into board_tb(`tip`, `created_at`, `finished_at`, `is_match`, `destination`, `request`, `user_id`, `store_id`) values ('1000', '2023-09-30 00:14', '2023-09-30 02:24', 'N', '전남대 공대7 217호관', '빨리 와주세요', '2', '1');

insert into board_tb(`tip`, `created_at`, `finished_at`, `is_match`, `destination`, `request`, `user_id`, `store_id`) values ('1000', '2023-09-30 00:14', '2023-09-30 02:24', 'N', '전남대 공대7 217호관', '빨리 와주세요', '2', '1');

insert into beverage_tb(`board_id`, `name`) values ('1', '핫 아메리카노');

insert into beverage_tb(`board_id`, `name`) values ('1', '아이스 아메리카노');

insert into beverage_tb(`board_id`, `name`) values ('2', '핫 아메리카노');

insert into beverage_tb(`board_id`, `name`) values ('3', '아이스 아메리카노');

insert into beverage_tb(`board_id`, `name`) values ('4', '핫 아메리카노');

insert into beverage_tb(`board_id`, `name`) values ('4', '아이스 아메리카노');

insert into match_tb(`id`, `arrival_time`, `match_time`, `user_id`) values ('1', '5', '2023-09-30 02:34', '2');

update board_tb set `is_match` = 'Y', `match_id` = '1' where `id` = '1';