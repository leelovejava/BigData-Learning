package com.leelovejava.kafka.kafka110.partitioner.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.Random;

/**
 * @Title PartitionerProducer.java
 * @Description 测试自定义分区器
 * @Author YangYunhe
 * @Date 2018-06-25 15:10:04
 */
public class PartitionerProducer {

    private static final String[] PHONE_NUMS = new String[]{
            "10000", "10000", "11111", "13700000003", "13700000004",
            "10000", "15500000006", "11111", "15500000008",
            "17600000009", "10000", "17600000011"
    };

    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.42.89:9092,192.168.42.89:9093,192.168.42.89:9094");
        // 设置分区器
        props.put("partitioner.class", "com.bonc.rdpe.kafka110.partitioner.PhonenumPartitioner");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);

        int count = 0;
        int length = PHONE_NUMS.length;

        while (count < 10) {
            Random rand = new Random();
            String phoneNum = PHONE_NUMS[rand.nextInt(length)];
            ProducerRecord<String, String> record = new ProducerRecord<>("dev3-yangyunhe-topic001", phoneNum, phoneNum);
            RecordMetadata metadata = producer.send(record).get();
            String result = "phonenum [" + record.value() + "] has been sent to partition " + metadata.partition();
            System.out.println(result);
            Thread.sleep(500);
            count++;
        }
        producer.close();
    }
}