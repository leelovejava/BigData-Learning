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
package com.leelovejava.stream.drpc;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.LocalDRPC;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

/**
 * This topology is a basic example of doing distributed RPC on top of Storm. It
 * implements a function that appends a "!" to any string you send the DRPC
 * function.
 * <p/>
 * See https://github.com/nathanmarz/storm/wiki/Distributed-RPC for more
 * information on doing distributed RPC on top of Storm.
 */
public class BasicDRPCTopology {
    public static class ExclaimBolt extends BaseBasicBolt {

        public void execute(Tuple tuple, BasicOutputCollector collector) {
            String input = tuple.getString(1);
            collector.emit(new Values(tuple.getValue(0), input + "!"));
        }


        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("id", "result"));
        }

    }

    /**
     * 运行模式之远程
     * 1).远程模式（集群模式）
     *
     * 2).修改配置文件conf/storm.yaml
     * drpc.servers:
     * - "node1“
     *
     * 3).启动DRPC Server
     * bin/storm drpc &
     *
     * 4).通过StormSubmitter.submitTopology提交拓扑
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //LinearDRPCTopologyBuilder builder = new LinearDRPCTopologyBuilder("exclamation");
        //builder.addBolt(new ExclaimBolt(), 3);
        /**
         * 定义定义DRPC拓扑 方法2：
         * 直接通过普通的拓扑构造方法TopologyBuilder来创建DRPC拓扑
         * 需要手动设定好开始的DRPCSpout以及结束的ReturnResults
         */
        TopologyBuilder builder = new TopologyBuilder();
        builder.setBolt("exclaimBolt", new ExclaimBolt(), 3);

        Config conf = new Config();

        if (args == null || args.length == 0) {
            LocalDRPC drpc = new LocalDRPC();
            LocalCluster cluster = new LocalCluster();

            cluster.submitTopology("drpc-demo", conf, builder.createTopology());

            for (String word : new String[]{"hello", "goodbye"}) {
                System.err.println("Result for \"" + word + "\": " + drpc.execute("exclamation", word));
            }

            cluster.shutdown();
            drpc.shutdown();
        } else {
            conf.setNumWorkers(3);
            // 通过StormSubmitter.submitTopology提交拓扑
            StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
        }
    }
}
