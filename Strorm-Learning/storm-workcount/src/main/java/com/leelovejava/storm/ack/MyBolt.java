package com.leelovejava.storm.ack;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;



public class MyBolt implements IRichBolt {

	private static final long serialVersionUID = 1L;

	OutputCollector collector = null;


	int num = 0;
	String valueString = null;

	public void execute(Tuple input) {
		try {
			valueString = input.getStringByField("log") ;
			
			if(valueString != null) {
				num ++ ;
				System.err.println(Thread.currentThread().getName()+"   lines  :"+num +"   session_id:"+valueString.split("\t")[1]);
			}
			collector.emit(input, new Values(valueString));
//			collector.emit(new Values(valueString));
			collector.ack(input);
			Thread.sleep(2000);
		} catch (Exception e) {
			collector.fail(input);
			e.printStackTrace();
		}
		
	}


	public void cleanup() {

	}


	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector ;
	}


	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("session_id")) ;
	}


	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
