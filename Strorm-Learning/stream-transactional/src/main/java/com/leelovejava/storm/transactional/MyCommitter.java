package com.leelovejava.storm.transactional;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.apache.storm.coordination.BatchOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseTransactionalBolt;
import org.apache.storm.transactional.ICommitter;
import org.apache.storm.transactional.TransactionAttempt;
import org.apache.storm.tuple.Tuple;

public class MyCommitter extends BaseTransactionalBolt implements ICommitter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String GLOBAL_KEY = "GLOBAL_KEY";
	public static Map<String, DbValue> dbMap = new HashMap<String, DbValue>();
	int sum = 0;
	TransactionAttempt id;
	BatchOutputCollector collector;


	public void execute(Tuple tuple) {
		sum += tuple.getInteger(1);
	}


	public void finishBatch() {

		DbValue value = dbMap.get(GLOBAL_KEY);
		DbValue newValue;
		if (value == null || !value.txid.equals(id.getTransactionId())) {
			// 更新数据库
			newValue = new DbValue();
			newValue.txid = id.getTransactionId();
			if (value == null) {
				newValue.count = sum;
			} else {
				newValue.count = value.count + sum;
			}
			dbMap.put(GLOBAL_KEY, newValue);
		} else {
			newValue = value;
		}
		System.out.println("total==========================:" + dbMap.get(GLOBAL_KEY).count);
		// collector.emit(tuple)
	}


	public void prepare(Map conf, TopologyContext context, BatchOutputCollector collector, TransactionAttempt id) {
		this.id = id;
		this.collector = collector;
	}


	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

	public static class DbValue {
		BigInteger txid;
		int count = 0;
	}

}
