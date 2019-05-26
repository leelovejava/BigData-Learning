package com.leelovejava.storm.transactional;

import java.util.Map;

import org.apache.storm.coordination.BatchOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseTransactionalBolt;
import org.apache.storm.transactional.TransactionAttempt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class MyTransactionBolt extends BaseTransactionalBolt {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    Integer count = 0;
    BatchOutputCollector collector;
    TransactionAttempt tx;


    public void prepare(Map conf, TopologyContext context,
                        BatchOutputCollector collector, TransactionAttempt id) {
        this.collector = collector;
        System.err.println("MyTransactionBolt prepare txid:" + id.getTransactionId() + ";  attemptid: " + id.getAttemptId());
    }

    /**
     * 处理batch中每一个tuple
     */

    public void execute(Tuple tuple) {

        tx = (TransactionAttempt) tuple.getValue(0);
        System.err.println("MyTransactionBolt TransactionAttempt txid:" + tx.getTransactionId() + ";  attemptid:" + tx.getAttemptId());
        String log = tuple.getString(1);
        if (log != null && log.length() > 0) {
            count++;
        }
    }

    /**
     * 同一个batch处理完成后，会调用一次finishBatch方法
     */

    public void finishBatch() {
        System.err.println("finishBatch: " + count);
        collector.emit(new Values(tx, count));
    }


    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("tx", "count"));
    }

}
