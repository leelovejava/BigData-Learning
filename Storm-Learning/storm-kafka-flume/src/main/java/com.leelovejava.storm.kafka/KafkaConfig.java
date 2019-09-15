package com.leelovejava.storm.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.storm.kafka.spout.Func;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff;
import org.apache.storm.kafka.spout.KafkaSpoutRetryService;
import org.apache.storm.kafka.spout.trident.KafkaTridentSpoutOpaque;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.trident.spout.ITridentDataSource;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.apache.storm.kafka.spout.KafkaSpoutConfig.FirstPollOffsetStrategy.EARLIEST;

/**
 * kafka配置
 */
public class KafkaConfig {
    /**
     * kafka重试
     *
     * @return
     */
    protected KafkaSpoutRetryService newRetryService() {
        return new KafkaSpoutRetryExponentialBackoff(new KafkaSpoutRetryExponentialBackoff.TimeInterval(500L, TimeUnit.MICROSECONDS),
                KafkaSpoutRetryExponentialBackoff.TimeInterval.milliSeconds(2), Integer.MAX_VALUE, KafkaSpoutRetryExponentialBackoff.TimeInterval.seconds(10));
    }

    private static Func<ConsumerRecord<String, String>, List<Object>> JUST_VALUE_FUNC = new JustValueFunc();

    /**
     * Needs to be serializable
     */
    private static class JustValueFunc implements Func<ConsumerRecord<String, String>, List<Object>>, Serializable {
        @Override
        public List<Object> apply(ConsumerRecord<String, String> record) {
            return new Values(record.value());
        }
    }

    /**
     * kafka的Spout配置
     *
     * @param brokerHosts kafka集群列表,例如`localhost:9092`
     * @param topicName   要消费的topic主题,支持多个,例如`test-trident`
     * @return
     */
    public KafkaSpoutConfig<String, String> newKafkaSpoutConfig(String brokerHosts, String topicName) {
        return KafkaSpoutConfig.builder(brokerHosts, topicName)
                // 分组
                .setProp(ConsumerConfig.GROUP_ID_CONFIG, "kafkaSpoutTestGroup_" + System.nanoTime())
                // consumer每次发起fetch请求时，读取到的数据的限制,每次fetch200个record
                .setProp(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 200)
                // 配置具有要在默认流上发出的元组的转换器
                .setRecordTranslator(JUST_VALUE_FUNC, new Fields("str"))
                // 重试
                .setRetry(newRetryService())
                .setOffsetCommitPeriodMs(10_000)
                // 设置进程启动时Kafka spout在第一次轮询中用于Kafka代理的偏移量 重头开始小时
                .setFirstPollOffsetStrategy(EARLIEST)
                .setMaxUncommittedOffsets(250)
                .build();
    }
}
