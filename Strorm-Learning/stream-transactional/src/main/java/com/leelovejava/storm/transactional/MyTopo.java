package com.leelovejava.storm.transactional;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.transactional.TransactionalTopologyBuilder;

public class MyTopo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 事务
		TransactionalTopologyBuilder builder = new TransactionalTopologyBuilder("ttbId","spoutid",new MyTxSpout(),1);
		builder.setBolt("bolt1", new MyTransactionBolt(),3).shuffleGrouping("spoutid");
		builder.setBolt("committer", new MyCommitter(),1).shuffleGrouping("bolt1") ;
		
		Config conf = new Config() ;
		conf.setDebug(false);

		if (args.length > 0) {
			try {
				StormSubmitter.submitTopology(args[0], conf, builder.buildTopology());
			} catch (AlreadyAliveException e) {
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				e.printStackTrace();
			} catch (AuthorizationException e) {
				e.printStackTrace();
			}
		}else {
			LocalCluster localCluster = new LocalCluster();
			localCluster.submitTopology("mytopology", conf, builder.buildTopology());
		}
	}
}
