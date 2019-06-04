package com.leelovejava.storm.grouping;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

public class MyBolt implements IRichBolt {

	private static final long serialVersionUID = 1L;

	OutputCollector collector = null;
	int num = 0;
	String valueString = null;

	@Override
	public void cleanup() {

	}

	@Override
	public void execute(Tuple input) {
		try {
			valueString = input.getStringByField("log");

			if (valueString != null) {
				num++;
				System.err.println(input.getSourceStreamId() + " " + Thread.currentThread().getName() + "--id="
						+ Thread.currentThread().getId() + "   lines  :" + num + "   session_id:"
						+ valueString.split("\t")[1]);
			}
			collector.ack(input);
			// Thread.sleep(2000);
		} catch (Exception e) {
			collector.fail(input);
			e.printStackTrace();
		}

	}
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(""));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
