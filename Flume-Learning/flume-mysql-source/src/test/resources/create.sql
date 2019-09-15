CREATE DATABASE mysqlsource;

USE mysqlsource;

CREATE TABLE `student` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` varchar(255) NOT NULL,
 PRIMARY KEY (`id`)
);

CREATE TABLE `flume_meta` (
`source_tab` varchar(255) NOT NULL,
`currentIndex` varchar(255) NOT NULL,
 PRIMARY KEY (`source_tab`)
);

insert into `student`(name) values ('zhangsan');
insert into `student`(name) values ('lisi');
insert into `student`(name) values ('wangwu');
insert into `student`(name) values ('zhaoliu');