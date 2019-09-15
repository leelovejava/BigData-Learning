package com.cmcc.topo;

import com.cmcc.tools.DateFmt;
import com.cmcc.constant.*;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.hbase.bolt.mapper.HBaseValueMapper;
import org.apache.storm.hbase.trident.mapper.SimpleTridentHBaseMapper;
import org.apache.storm.hbase.trident.mapper.TridentHBaseMapper;
import org.apache.storm.hbase.trident.state.HBaseState;
import org.apache.storm.hbase.trident.state.HBaseStateFactory;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.builtin.FirstN;
import org.apache.storm.trident.state.StateFactory;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 2016/4/13 0013.
 */
public class TridentOneCellMonintorTopology {

    public static class CellFilter extends BaseFunction {
        private static final long serialVersionUID = 1L;
        // 通话总数
        Map<String, Long> cellCountMap = new HashMap<String, Long>();
        // 掉话数
        Map<String, Long> cellDropCountMap = new HashMap<String, Long>();
        long beginTime = System.currentTimeMillis();
        long endTime = 0;
        String todayStr = null;

        @Override
        public void execute(TridentTuple tuple, TridentCollector collector) {
            if (tuple == null) {
                return;
            }
            String line = tuple.getString(0);
            String dateStr = null;
            String cellNum = null;
            String dropNum = null;
            try {
                String[] str = line.split("\\t");
                // 发出的数据 时间,小区编号，掉话状态
                dateStr = DateFmt.getCountDate(str[2], DateFmt.date_short);
                cellNum = str[1];
                dropNum = str[3];
            } catch (Exception e) {
                e.printStackTrace();
            }

            todayStr = DateFmt.getCountDate(null, DateFmt.date_short);
            // 跨天的处理
            if (todayStr != dateStr && todayStr.compareTo(dateStr) < 0) {
                cellCountMap.clear();
                cellDropCountMap.clear();
            }

            Long cellAll = cellCountMap.get(cellNum);
            if (cellAll == null) {
                cellAll = 0L;
            }

            cellAll++;
            cellCountMap.put(cellNum, cellAll);
            Long cellDropAll = cellDropCountMap.get(cellNum);
            int t = Integer.parseInt(dropNum);
            if (t > 0) {
                if (cellDropAll == null) {
                    cellDropAll = 0L;
                }
                cellDropAll++;
                cellDropCountMap.put(cellNum, cellDropAll);
            }
            // 每隔五秒统计一次掉话率
            endTime = System.currentTimeMillis();
            if (endTime - beginTime >= 5000) {
                String today_minute = DateFmt.getCountDate(null, DateFmt.date_minute);
                String[] arr = this.getAxsi();
                long dropnum = cellDropCountMap.containsKey(cellNum) ? cellDropCountMap.get(cellNum) : 0;
                double drop_rate = dropnum / Double.valueOf(cellCountMap.get(cellNum));

                collector.emit(new Values(drop_rate, cellNum + "_" + todayStr, today_minute));
                // collector.emit(new
                // Values(cellNum+"_"+todayStr,"{"+"time_title:\""+arr[0]+"\",xAxis:"+
                // arr[1]+",call_num:"+cellCountMap.get(cellNum)+",call_drop_num:"+drop_rate+"}"));
                beginTime = System.currentTimeMillis();
            }

        }

        /**
         * 获取X坐标
         *
         * @return
         */
        public String[] getAxsi() {
            LocalDateTime localDate = LocalDateTime.now();
            // 时
            int hour = localDate.getDayOfMonth();
            // 分
            int minute = localDate.getMinute();
            // 秒
            int sec = localDate.getSecond();
            // 总秒数
            int curSecNum = hour * 3600 + minute * 60 + sec;

            Double xValue = (double) curSecNum / 3600;
            // 时间，数值
            String[] end = {hour + ":" + minute, xValue.toString()};
            return end;
        }
    }

    public static void main(String[] args) {
        KafkaSpoutConfig.Builder kafkaBuilder = new KafkaSpoutConfig.Builder(GlobalConstants.BROKER_LIST, GlobalConstants.TOPIC_NAME);
        KafkaSpoutConfig kafkaConfig = new KafkaSpoutConfig(kafkaBuilder);

        //tridentKafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        //tridentKafkaConfig.forceFromStart = false;
        KafkaSpout tridentKafkaSpout = new KafkaSpout(kafkaConfig);

        TridentHBaseMapper tridentHBaseMapper = new SimpleTridentHBaseMapper().withColumnFamily("cf")
                .withColumnFields(new Fields("today_minute", "drop_rate"))
                // .withCounterFields(new Fields("cell_num"))
                .withRowKeyField("cell_num");

        HBaseValueMapper rowToStormValueMapper = new CommonHbaseValueMapper();
        // create 'cell_monitor_drop_rate','cf'
        // 这里的hdfs://chenkl/hbase是hbase在hdfs的root路径
        HBaseState.Options options = new HBaseState.Options().withConfigKey("hdfs://chenkl/hbase")
                .withDurability(Durability.SYNC_WAL).withMapper(tridentHBaseMapper)
                .withRowToStormValueMapper(rowToStormValueMapper).withTableName("cell_monitor_drop_rate");

        StateFactory factory = new HBaseStateFactory(options);

        TridentTopology topology = new TridentTopology();
        // 这里可以和上面的代码组合统计某个基站的掉话率变化，这里注释了，但是存储的时候需要改变一下不能用SimpleTridentHBaseMapper存储了
        // Stream stream = topology.newStream(topicName, tridentKafkaSpout)
        // .each(new Fields("str"), new CellFilter(), new Fields("drop_rate",
        // "cell_num", "date_rate")).toStream();
        // stream.partitionPersist(factory,new Fields("cell_num","drop_rate",
        // "date_rate"),new HBaseUpdater(),new Fields());

        Stream stream = topology.newStream(GlobalConstants.TOPIC_NAME, tridentKafkaSpout)
                .each(new Fields("str"), new CellFilter(), new Fields("drop_rate", "cell_num", "today_minute"))
                .groupBy(new Fields("today_minute")).aggregate(new Fields("drop_rate", "cell_num"),
                        new FirstN.FirstNSortedAgg(10, "drop_rate", false), new Fields("drop_rate", "cell_num"))
                .toStream();
        stream.partitionPersist(factory, new Fields("cell_num", "drop_rate", "today_minute"), new HBaseUpdater(),
                new Fields());

        Config conf = new Config();
        conf.setDebug(true);
        conf.setNumWorkers(3);
        // StormSubmitter.submitTopologyWithProgressBar(topoName, conf,
        // topology.build());
        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology(GlobalConstants.TOPIC_NAME, conf, topology.build());
    }
}
