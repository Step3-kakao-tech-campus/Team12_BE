use pickup_shuttle;
create table beverage_tb
(
    board_id bigint       null,
    id       bigint auto_increment
        primary key,
    name     varchar(255) not null
);

create table board_tb
(
    tip         int                      not null,
    created_at  datetime(6)              not null,
    finished_at datetime(6)              not null,
    id          bigint auto_increment
        primary key,
    match_id    bigint                   null,
    store_id    bigint                   not null,
    user_id     bigint                   not null,
    destination varchar(255)             not null,
    is_match    varchar(255) default 'N' not null,
    request     varchar(255) default ''  not null
);

create table match_tb
(
    arrival_time int         not null,
    id           bigint auto_increment
        primary key,
    match_time   datetime(6) not null,
    user_id      bigint     not null
);

create table refresh_token
(
    id            bigint auto_increment
        primary key,
    user_id       bigint       null,
    refresh_token varchar(255) default '' not null
);



create table store_tb
(
    id   bigint auto_increment
        primary key,
    name varchar(255) null
);

create table user_tb
(
    role         tinyint      default 3  not null,
    id           bigint auto_increment
        primary key,
    account      varchar(255) default '' not null,
    bank         varchar(255) default '' not null,
    email        varchar(255) default '' not null,
    nickname     varchar(255) default '' not null,
    phone_number varchar(255) default '' not null,
    pwd          varchar(255)            null,
    social_id    varchar(255) default '' not null,
    uid          varchar(255)            null,
    url          varchar(255) default '' not null,
    check (`role` between 0 and 3)
);

