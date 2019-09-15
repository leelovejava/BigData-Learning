# Flume高级之自定义MySQLSource

## 1) 将MySql驱动包放入Flume的lib目录下

## 2) 打包项目并将Jar包放入Flume的lib目录下

## 3) MySql准备
create.sql

## 4) 配置文件准备
mysql.conf

## 5) 执行Flume
> bin/flume-ng agent --conf conf/ --name a1 \
--conf-file job/mysql.conf -Dflume.root.logger=INFO,console