CREATE TABLE IF NOT EXISTS posts (
    id numeric(20) NOT NULL,
    title varchar(150) NOT NULL,
    body varchar(350) NOT NULL,
    user_id numeric(20) NOT NULL,
    edited boolean default false,

    constraint post_pk primary key(id)
);

