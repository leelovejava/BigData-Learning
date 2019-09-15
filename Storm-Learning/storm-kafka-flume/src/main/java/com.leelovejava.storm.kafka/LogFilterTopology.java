/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.leelovejava.storm.kafka;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Arrays;
import java.util.Properties;

/**
 * This topology demonstrates Storm's stream groupings and multilang
 * capabilities.
 * <p>
 * kafka+storm https://www.cnblogs.com/freeweb/p/5292961.html
 *
 * @see `http://storm.apache.org/releases/2.0.0-SNAPSHOT/storm-kafka.html`
 */
public class LogFilterTopology {

    public static class FilterBolt extends BaseBasicBolt {

        public void execute(Tuple tuple, BasicOutputCollector collector) {
            String line = tuple.getString(0);
            System.err.println("Accept:  " + line);
            // 包含ERROR的行留下
            if (line.contains("ERROR")) {
                System.err.println("Filter:  " + line);
                collector.emit(new Values(line));
            }
        }


        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            // 定义message提供给后面FieldNameBasedTupleToKafkaMapper使用
            declarer.declare(new Fields("message"));
        }
    }

    public static void main(String[] args) throws Exception {

        TopologyBuilder builder = new TopologyBuilder();
        String bootstrapServers = "hadoop000:9092";
        // config kafka spout，话题
        String topic = "testflume";

        KafkaConfig kafkaConfig = new KafkaConfig();
        KafkaSpoutConfig kafkaSpoutConfig = kafkaConfig.newKafkaSpoutConfig(bootstrapServers, topic);
        org.apache.storm.kafka.spout.KafkaSpout kafkaSpout = new org.apache.storm.kafka.spout.KafkaSpout(kafkaSpoutConfig);
        builder.setSpout("kafka_spout", kafkaSpout, 3);

        // 数据写出
        // set kafka bolt
        // withTopicSelector使用缺省的选择器指定写入的topic： LogError
        // withTupleToKafkaMapper tuple==>kafka的key和message
        //org.apache.storm.kafka.bolt.KafkaBolt kafka_bolt = new KafkaBolt().withTopicSelector(new DefaultTopicSelector("LogError"))
        //        .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper());

        //set producer properties.
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "1");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaBolt kafka_bolt = new KafkaBolt()
                .withProducerProperties(props)
                .withTopicSelector(new DefaultTopicSelector("test"))
                .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper());


        builder.setBolt("kafka_bolt", kafka_bolt, 2).shuffleGrouping("filter");

        Config conf = new Config();
        // set producer properties.


        /**
         * Kafka生产者ACK机制 0 ： 生产者不等待Kafka broker完成确认，继续发送下一条数据 1 ：
         * 生产者等待消息在leader接收成功确认之后，继续发送下一条数据 -1 ：
         * 生产者等待消息在follower副本接收到数据确认之后，继续发送下一条数据
         */
        props.put("request.required.acks", "1");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        conf.put("kafka.broker.properties", props);

        conf.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(new String[]{"node1", "node2", "node3"}));

        // 本地方式运行
        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("mytopology", conf, builder.createTopology());

    }
}
