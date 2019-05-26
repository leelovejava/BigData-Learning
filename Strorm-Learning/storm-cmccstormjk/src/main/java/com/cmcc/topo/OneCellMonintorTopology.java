package com.cmcc.topo;

import com.cmcc.kafka.productor.KafkaProperties;
import org.apache.storm.generated.AuthorizationException;
import com.cmcc.spout.LogSpout;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import com.cmcc.bolt.CellDaoltBolt;
import com.cmcc.bolt.CellFilterBolt;

public class OneCellMonintorTopology {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("spout", new LogSpout(KafkaProperties.Cell_Topic), 3);
		builder.setBolt("cellBolt", new CellFilterBolt(), 3).shuffleGrouping("spout");
		builder.setBolt("CellDaoltBolt", new CellDaoltBolt() , 5).fieldsGrouping("cellBolt", new Fields("cell_num")) ;
		
		Config conf = new Config() ;
//		conf.setDebug(true);
		conf.setDebug(false);
		conf.setNumWorkers(10);
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
		}else {
			LocalCluster localCluster = new LocalCluster();
			localCluster.submitTopology("mytopology", conf, builder.createTopology());
		}
		
		
	}

}
