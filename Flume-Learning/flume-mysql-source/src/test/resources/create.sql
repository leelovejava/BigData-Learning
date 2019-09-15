CREATE DATABASE mysqlsource;

USE mysqlsource;

CREATE TABLE `student` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` varchar(255) NOT NULL,
 PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='学生表';;

CREATE TABLE `flume_meta` (
`source_tab` varchar(255) NOT NULL,
`currentIndex` varchar(255) NOT NULL,
 PRIMARY KEY (`source_tab`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='flume元数据表';;

insert into `student`(name) values ('zhangsan');
insert into `student`(name) values ('lisi');
insert into `student`(name) values ('wangwu');
insert into `student`(name) values ('zhaoliu');