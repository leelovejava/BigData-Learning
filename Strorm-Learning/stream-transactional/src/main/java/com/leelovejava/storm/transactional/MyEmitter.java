package com.leelovejava.storm.transactional;

import java.math.BigInteger;
import java.util.Map;

import org.apache.storm.coordination.BatchOutputCollector;
import org.apache.storm.transactional.ITransactionalSpout;
import org.apache.storm.transactional.TransactionAttempt;
import org.apache.storm.tuple.Values;

public class MyEmitter implements ITransactionalSpout.Emitter<MyMeta>{

	Map<Long, String> dbMap  = null;
	public MyEmitter(Map<Long, String> dbMap) {
		this.dbMap = dbMap;
	}
	

	public void cleanupBefore(BigInteger txid) {
		
	}


	public void close() {
		
	}

	/**
	 * 发送tuple的batch
	 */
	public void emitBatch(TransactionAttempt tx, MyMeta coordinatorMeta,
			BatchOutputCollector collector) {
		
		long beginPoint = coordinatorMeta.getBeginPoint() ;
		int num = coordinatorMeta.getNum() ;
		
		for (long i = beginPoint; i < num+beginPoint; i++) {
			if (dbMap.get(i)==null) {
				continue;
			}
			/**
			 * 必须以TransactionAttempt第一位发送
			 * _txid： transaction id 每组batch中的tuple必须为同一id，不论replay多少次
			 * _attemptId
			 */
			collector.emit(new Values(tx,dbMap.get(i)));
		}
	}

}
