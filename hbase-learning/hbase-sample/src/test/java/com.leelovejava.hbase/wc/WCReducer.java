package com.leelovejava.hbase.wc;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class WCReducer extends TableReducer<Text, IntWritable, ImmutableBytesWritable> {

	@Override
	protected void reduce(Text text, Iterable<IntWritable> iterable, Context context)
			throws IOException, InterruptedException {

		int sum = 0;
		for (IntWritable it : iterable) {
			sum += it.get();
		}
		Put put = new Put(text.toString().getBytes());
		put.addColumn("cf".getBytes(), "ct".getBytes(), (sum + "").getBytes());
		context.write(null, put);

	}
}
