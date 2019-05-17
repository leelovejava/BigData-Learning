package com.leelovejava.hbase.wc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

/**
 * hbase和mqpreduce整合
 */
public class WCRunner {
	/**
	 *
	 * hadoop  fs  -put  ~/data/workCount.txt  /user/hive/warehouse/wc
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		///conf.set("fs.defaultFS", "hdfs://node1:8020");
		conf.set("fs.defaultFS", "hdfs://hadoop000:8020");
		//conf.set("hbase.zookeeper.quorum", "node1,node2,node3");
		conf.set("hbase.zookeeper.quorum", "hadoop000:2181");
		Job job = Job.getInstance(conf);
		job.setJarByClass(WCRunner.class);

		// 指定mapper 和 reducer
		job.setMapperClass(com.leelovejava.hbase.wc.WCMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		// 最后一个参数设置false
		// TableMapReduceUtil.initTableReducerJob(table, reducer, job);
		TableMapReduceUtil.initTableReducerJob("wc", com.leelovejava.hbase.wc.WCReducer.class, job, null, null, null, null, false);
		FileInputFormat.addInputPath(job, new Path("/user/hive/warehouse/wc/"));
		job.waitForCompletion(true);
	}
}
