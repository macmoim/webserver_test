phpmyadmin 에서 "db_chat_member_test"이름으로 db를 만든 후 아래 sql문으로 table을 생성한다.

create table member(
id int primary key auto_increment,
user_id varchar(12) unique,
password varchar(10),
user_alias varchar(100),
email varchar(100)
); 

CREATE TABLE images( 
id int auto_increment, 
image longblob, 
title varchar(100), 
width int(8), 
height int(8), 
filesize int(8), 
img_type VARCHAR(40),
PRIMARY KEY (id) 
);