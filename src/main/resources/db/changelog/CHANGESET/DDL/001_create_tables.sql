create table users(
    id uuid not null primary key,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    password   varchar(255) not null,
    patronymic_name varchar(255) not null,
    role varchar(255)
    check ((role)::text = ANY ((ARRAY ['USER'::character varying, 'ADMIN'::character varying])::text[])),
    username varchar(255) not null unique
);

create table card(
    id uuid not null primary key,
    balance integer not null,
    card_number varchar(255) not null,
    expiry_date date not null,
    status varchar(255) check ((status)::text = ANY
    ((ARRAY ['ACTIVE'::character varying, 'BLOCKED'::character varying, 'EXPIRED'::character varying])::text[])),
    user_id uuid not null references users
);

create table request_to_block(
    id uuid not null primary key,
    card_id uuid unique references card
);

