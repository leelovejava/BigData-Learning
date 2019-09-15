package com.cmcc.spout;

import com.cmcc.kafka.consumers.CellConsumer;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LogSpout implements IRichSpout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String topic;
	
	public LogSpout(String topic)
	{
		this.topic = topic ;
	}
	SpoutOutputCollector collector = null;
	Queue<String> queue = new ConcurrentLinkedQueue<String>() ;
	
	@Override
	public void ack(Object msgId) {
		// 通常用于删除已经成功处理的tuple
		// 我们这里不用实现
	}

	@Override
	public void activate() {
	}

	@Override
	public void close() {
	}

	@Override
	public void deactivate() {

	}

	@Override
	public void fail(Object msgId) {
	}

	@Override
	public void nextTuple() {
		if (queue.size() > 0) {
			String str = queue.poll() ;
			collector.emit(new Values(str),UUID.randomUUID().toString()) ;
			Utils.sleep(500);
		}
	}

	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this.collector = collector ;
		CellConsumer consumer = new CellConsumer(topic) ;
		consumer.start() ;
		queue = consumer.getQueue() ;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("log")) ;
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
