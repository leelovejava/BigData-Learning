package com.atguigu.structured;

import net.sf.json.JSONObject;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.lang.Thread.sleep;

/*
  1. 分区

  2. 数据本地性

  3. 公平调度
 */
public class KafkaProducer010 {
    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", "mt-mdh.local:9093");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
        Producer<String, String> producer = new org.apache.kafka.clients.producer.KafkaProducer<String, String>(props);


        SendMsg_Split(producer);
        producer.close();
    }

    public static void SendMsg2foo(Producer<String, String> producer) {
        for (int i = 0; i < 3000; i++) {

            producer.send(new ProducerRecord<String, String>("foo", 1, String.valueOf(i), "hadhada hadhah"));

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void SendMsg_Split(Producer<String, String> producer) {
        java.util.Random points = new java.util.Random(2);
        List list = new ArrayList<String>();
        list.add("apple");
        list.add("pear");
        list.add("nut");
        list.add("grape");
        list.add("banana");
        list.add("pineapple");
        list.add("pomelo");
        list.add("orange");


        for (int i = 0; i < 3000; i++) {

            producer.send(new ProducerRecord<String, String>("split_test", String.valueOf(i), list.get(points.nextInt(7)) + " " + list.get(points.nextInt(7))));

            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void SendMsg_Json(Producer<String, String> producer) {
        java.util.Random points = new java.util.Random(2);
        List list = new ArrayList<String>();
        list.add("apple");
        list.add("pear");
        list.add("nut");
        list.add("grape");
        list.add("banana");
        list.add("pineapple");
        list.add("pomelo");
        list.add("orange");


        for (int i = 0; i < 3000; i++) {
            JSONObject json = new JSONObject();
            json.put("fruit", list.get(points.nextInt(8)));
            json.put("time", System.currentTimeMillis());
            producer.send(new ProducerRecord<String, String>("jsontest", String.valueOf(i), json.toString()));

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void SendMsg_Stream_Stream_join(Producer<String, String> producer) {
        java.util.Random points = new java.util.Random(2);
        List list = new ArrayList<String>();
        list.add("000000");
        list.add("000001");
        list.add("000002");
        list.add("000003");
        list.add("000004");
        list.add("000005");
        list.add("000006");
        list.add("000007");
        list.add("000008");
        list.add("000009");
        list.add("000010");
        list.add("000011");
        list.add("000012");
        for (int i = 0; i < 3000; i++) {
            producer.send(new ProducerRecord<String, String>("Stream_Static_Join", String.valueOf(i), (String) list.get(points.nextInt(8))));
            producer.send(new ProducerRecord<String, String>("Stream_Steam_Join", String.valueOf(i), (String) list.get(points.nextInt(8))));

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void SendMsg_Stream_Static_join(Producer<String, String> producer) {
        java.util.Random points = new java.util.Random(2);
        List list = new ArrayList<String>();
        list.add("000000");
        list.add("000001");
        list.add("000002");
        list.add("000003");
        list.add("000004");
        list.add("000005");
        list.add("000006");
        list.add("000007");
        list.add("000008");
        list.add("000009");
        list.add("000010");
        list.add("000011");
        list.add("000012");
        for (int i = 0; i < 3000; i++) {
            producer.send(new ProducerRecord<String, String>("Stream_Static_Join", String.valueOf(i), (String) list.get(points.nextInt(8))));
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
