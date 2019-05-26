package com.cmcc.topo;

import com.cmcc.bolt.CellDaoltBolt;
import com.cmcc.bolt.CellFilterBolt;
import com.cmcc.hbase.constant.Constants;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

/**
 * kafka消费
 */
public class KafkaOneCellMonintorTopology {

    /**
     * @param args
     */
    public static void main(String[] args) {

        TopologyBuilder builder = new TopologyBuilder();

        KafkaSpoutConfig.Builder kafkaBuilder = new KafkaSpoutConfig.Builder(Constants.BROKER_LIST, Constants.TOPIC_NAME);
        KafkaSpoutConfig kafkaConfig = new KafkaSpoutConfig(kafkaBuilder);

        builder.setSpout("spout", new KafkaSpout(kafkaConfig), 3);
        builder.setBolt("cellBolt", new CellFilterBolt(), 3).shuffleGrouping("spout");
        builder.setBolt("CellDaoltBolt", new CellDaoltBolt(), 5)
                .fieldsGrouping("cellBolt", new Fields("cell_num"));


        Config conf = new Config();
        conf.setDebug(false);
        conf.setNumWorkers(5);
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
            System.out.println("Local running");
            LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology("mytopology", conf, builder.createTopology());
        }

    }

}
