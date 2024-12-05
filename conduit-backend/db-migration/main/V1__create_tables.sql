-- V1__CreateUsersTable.sql
create table users
(
    id       serial primary key,
    email    varchar(255) not null unique,
    username varchar(255) not null unique,
    password varchar(255) not null,
    bio      varchar(4096),
    image    varchar(255)
);
