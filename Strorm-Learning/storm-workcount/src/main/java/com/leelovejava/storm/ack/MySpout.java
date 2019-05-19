package com.leelovejava.storm.ack;


import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class MySpout implements IRichSpout {

    private static final long serialVersionUID = 1L;

    int index = 0;

    FileInputStream fis;
    InputStreamReader isr;
    BufferedReader br;
    SpoutOutputCollector collector = null;
    String str = null;


    public void nextTuple() {
        try {
            if ((str = this.br.readLine()) != null) {
                // 过滤动作
                index++;
                collector.emit(new Values(str), index);
//				collector.emit(new Values(str));
            }
        } catch (Exception e) {
        }


    }

    public void close() {
        try {
            br.close();
            isr.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void open(Map conf, TopologyContext context,
                     SpoutOutputCollector collector) {
        try {
            this.collector = collector;
            this.fis = new FileInputStream("track.log");
            this.isr = new InputStreamReader(fis, "UTF-8");
            this.br = new BufferedReader(isr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("log"));
    }


    public Map<String, Object> getComponentConfiguration() {
        return null;
    }


    public void ack(Object msgId) {
        System.err.println(" [" + Thread.currentThread().getName() + "] " + " spout ack:" + msgId.toString());
    }


    public void activate() {

    }


    public void deactivate() {

    }

    public void fail(Object msgId) {
        System.err.println(" [" + Thread.currentThread().getName() + "] " + " spout fail:" + msgId.toString());
    }

}
