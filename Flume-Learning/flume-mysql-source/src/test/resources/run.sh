bin/flume-ng agent --conf conf/ --name a1 \
--conf-file job/mysql.conf -Dflume.root.logger=INFO,console