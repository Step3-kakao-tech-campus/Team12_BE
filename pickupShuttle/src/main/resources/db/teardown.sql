-- 모든 제약 조건 비활성화
SET REFERENTIAL_INTEGRITY FALSE;
truncate table user_tb;
SET REFERENTIAL_INTEGRITY TRUE;
-- 모든 제약 조건 활성화
INSERT INTO user_tb (`uid`,`pwd`,`nickname`, `phone_number`, `name`) VALUES ('testid', '{noop}1234', 'normal', '010-0000-0000','테스트');
INSERT INTO user_tb (`uid`,`pwd`,`nickname`, `phone_number`, `name`) VALUES ('honggildong', '{bcrypt}$2a$10$8H0OT8wgtALJkig6fmypi.Y7jzI5Y7W9PGgRKqnVeS2cLWGifwHF2', 'pickupmaster', '010-0000-0000','홍길동');

INSERT INTO account_tb(`user_id`, `number`, `bank`) VALUES ('1', '12691037512507', '하나');
