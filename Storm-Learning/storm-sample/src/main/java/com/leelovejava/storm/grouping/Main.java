package com.leelovejava.storm.grouping;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		TopologyBuilder builder = new TopologyBuilder();

		/**
		 * 设置Executor线程数
		 * TopologyBuilder.setSpout(String id, IRichSpout spout, Number parallelism_hint)
		 * TopologyBuilder.setBolt(String id, IRichBolt bolt, Number parallelism_hint)
		 * ：其中， parallelism_hint即为executor线程数
		 */
		builder.setSpout("spout", new MySpout(), 1);

		// shuffleGrouping其实就是随机往下游去发,不自觉的做到了负载均衡
//		builder.setBolt("bolt", new MyBolt(), 2).shuffleGrouping("spout");

		// fieldsGrouping其实就是MapReduce里面理解的Shuffle,根据fields求hash来取模
//		builder.setBolt("bolt", new MyBolt(), 2).fieldsGrouping("spout", new Fields("session_id"));

		// 全局分组,只往一个里面发,往taskId小的那个里面去发送
//		builder.setBolt("bolt", new MyBolt(), 2).globalGrouping("spout");

		// 不分组,等于shuffleGrouping
//		builder.setBolt("bolt", new MyBolt(), 2).noneGrouping("spout");

		// 广播
		builder.setBolt("bolt", new MyBolt(), 2).allGrouping("spout");

		// Map conf = new HashMap();
		// conf.put(Config.TOPOLOGY_WORKERS, 4);
		Config conf = new Config();
		conf.setDebug(false);
		conf.setMessageTimeoutSecs(30);

		if (args.length > 0) {
			try {
				StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				e.printStackTrace();
			} catch (AuthorizationException e) {
				e.printStackTrace();
			}
		} else {
			LocalCluster localCluster = new LocalCluster();
			localCluster.submitTopology("mytopology", conf, builder.createTopology());
		}

	}

}
