
SET foreign_key_checks = 0;

truncate table user_tb;
truncate table board_tb;
truncate table store_tb;
truncate table beverage_tb;
truncate table match_tb;

SET foreign_key_checks = 1;

insert into user_tb(role, id, account, bank, email, nickname, phone_number, pwd, social_id, uid, url)
values
    (0,1,'000-0000-0000','신한은행','','노인','010-0000-0000','pwd','1111','pickup1','https://aws.amazon.com1/'),
    (1,2,'000-0000-0000','신한은행','','배경','010-0000-0000','pwd','1112','pickup2','https://aws.amazon.com2/'),
    (2,3,'000-0000-0000','신한은행','','기준','010-0000-0000','pwd','1113','pickup3','https://aws.amazon.com3/'),
    (0,4,'000-0000-0000','신한은행','','효원','010-0000-0000','pwd','1114','pickup4','https://aws.amazon.com4/'),
    (1,5,'000-0000-0000','신한은행','','단빈','010-0000-0000','pwd','1115','pickup5','https://aws.amazon.com5/'),
    (2,6,'000-0000-0000','신한은행','','주현','010-0000-0000','pwd','1116','pickup6','https://aws.amazon.com6/'),
    (3,7,'000-0000-0000','신한은행','','일반인','010-0000-0000','pwd','1117','pickup7','');

insert into store_tb(id, name)
VALUES
    (1, '스타벅스'),
    (2, '컴포즈'),
    (3, '더벤티'),
    (4, '이디야'),
    (5, '메가MGC');

insert into board_tb (tip, created_at, finished_at, id, match_id, store_id, user_id, destination, is_match, request)
VALUES
    (1000, current_time,date_add(current_time, interval 30 minute), 1, null, 1, 1, '전남대 공대7 217호관', 'N', '빨리 와주세요1'),
    (1500, current_time,date_add(current_time, interval 30 minute), 2, null, 2, 2, '전남대 공대7 218호관', 'N', '빨리 와주세요2'),
    (2000, current_time,date_add(current_time, interval 30 minute), 3, null, 3, 3, '전남대 공대7 219호관', 'N', '빨리 와주세요3'),
    (1000, current_time,date_add(current_time, interval 30 minute), 4, null, 4, 4, '전남대 공대7 220호관', 'N', '빨리 와주세요4'),
    (1500, current_time,date_add(current_time, interval 30 minute), 5, null, 5, 5, '전남대 공대7 221호관', 'N', '빨리 와주세요5'),
    (2000, current_time,date_add(current_time, interval 30 minute), 6, null, 5, 6, '전남대 공대7 222호관', 'N', '빨리 와주세요6');

insert into beverage_tb(id, name, board_id)
values
    (1, '아메리카노', 3),
    (2, '카페라떼', 6);
insert into beverage_tb(id, name)
values
    (3, '카페모카'),
    (4, '초코라떼'),
    (5, '딸기라떼');

insert into match_tb (arrival_time, id, match_time, user_id)
VALUES
    (date_add(current_time(), interval 15 minute), 1, now(),3);

update board_tb set match_id = 1 , is_match = 'Y' where id = 3;