use pickup_shuttle;
create table beverage_tb
(
    board_id bigint null,
    id bigint auto_increment
        primary key,
    name varchar(255) not null,
    constraint FK6yu526ecbwatmsci8vjkvfgj4
        foreign key (board_id) references board_tb (id)
);

create table board_tb
(
    tip int not null,
    created_at datetime(6) not null,
    finished_at datetime(6) not null,
    id bigint auto_increment
        primary key,
    match_id bigint null,
    store_id bigint not null,
    user_id bigint not null,
    destination varchar(255) not null,
    is_match varchar(255) default 'N' not null,
    request varchar(255) default '' not null,
    constraint UK_e9ov5dodk6382dfw1t6gx9suq
        unique (match_id),
    constraint FKd6wnmg8e49vpsklaua5s5dorq
        foreign key (match_id) references match_tb (id),
    constraint FKgxwryj58kh66twbp656wo5gnn
        foreign key (user_id) references user_tb (id),
    constraint FKorkeits0mn7ck13ejdwhwdwn6
        foreign key (store_id) references store_tb (id)
);


create table match_tb
(
    arrival_time int not null,
    id bigint auto_increment
        primary key,
    match_time datetime(6) not null,
    user_id bigint not null,
    constraint FKb73i1rxgpq2rrwp04v2yi1j1t
        foreign key (user_id) references user_tb (id)
);

create table refresh_token_tb
(
    id bigint auto_increment
        primary key,
    user_id bigint not null,
    refresh_token varchar(255) not null,
    constraint UK_i88m3f1k3qibpak2hmnfqxacg
        unique (user_id),
    constraint FKh52omgh4spxvbf9uu2m1fpcgn
        foreign key (user_id) references user_tb (id)
);

create table store_tb
(
    id bigint auto_increment
        primary key,
    name varchar(255) null
);

create table user_tb
(
    role tinyint default 3 not null,
    id bigint auto_increment
        primary key,
    account varchar(255) default '' not null,
    bank varchar(255) default '' not null,
    email varchar(255) null,
    nickname varchar(255) not null,
    phone_number varchar(255) default '' not null,
    pwd varchar(255) null,
    social_id varchar(255) null,
    uid varchar(255) null,
    url varchar(255) default '' not null,
    check (`role` between 0 and 3)
);

