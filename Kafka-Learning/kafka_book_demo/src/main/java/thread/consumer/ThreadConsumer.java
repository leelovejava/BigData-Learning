package thread.consumer;

import kafka.utils.ShutdownableThread;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thread.KafkaProperties;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 多线程消费,消息有序
 * 文档 [使用多线程增加kafka消费能力] http://www.imooc.com/article/290122
 * 文档 [spring-kafka多线程顺序消费] https://blog.csdn.net/wk930106/article/details/102395152
 * 文档 [kafka系列 -- 多线程消费者实现] https://www.cnblogs.com/stillcoolme/p/9781064.html
 *
 * @author leelovejava
 */
public class ThreadConsumer extends ShutdownableThread {
    private static final Logger log = LoggerFactory.getLogger(ThreadConsumer.class);


    private final KafkaConsumer<Integer, String> consumer;
    private final String topic;

    ThreadPoolExecutor executor = new ThreadPoolExecutor(5,
            // 最大容量
            20,
            10,
            TimeUnit.SECONDS,
            // 零容量队列,一进一出，避免队列里缓冲数据，避免在系统异常关闭时，因为阻塞队列丢消息
            new SynchronousQueue<>(),
            // CallerRunsPolicy饱和策略，使得多线程处理不过来的时候，能够阻塞在kafka的消费线程上 在调用者线程执行
            new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 管道
     */
    private SynchronousQueue<ConsumerRecord<Integer, String>> pipelineQueue = new SynchronousQueue<>();

    public ThreadConsumer(String topic) {
        super("KafkaConsumerExample", false);
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProperties.KAFKA_SERVER_URL + ":" + KafkaProperties.KAFKA_SERVER_PORT);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer");
        ///props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        // 是否开启自动提交（offset）如果开启，consumer已经消费的offset信息将会间歇性的提交到kafka中（持久保存）
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        // 调用一次poll，返回的最大条数。这个值设置的大，那么处理的就慢，很容易超出max.poll.interval.ms的值（默认5分钟），造成消费者的离线。在耗时非常大的消费中，是需要特别注意的。
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 20);
        // 默认5分钟
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 10 * 60 * 1000);

        consumer = new KafkaConsumer<>(props);
        this.topic = topic;
    }



    /**
     * 我们把任务放入管道后，立马commit。如果线程池已经满了，将一直阻塞在消费者线程里，直到有空缺。然后，我们单独启动了一个线程，用来接收这些数据
     */
    void initPipelineThread() {
        new Thread(() -> {
            while (isRunning()) {
                try {
                    final ConsumerRecord<Integer, String> record = pipelineQueue.poll(5, TimeUnit.SECONDS);
                    if (null != record) {
                        executor.submit(() -> processRecordItem(record));
                    }
                } catch (InterruptedException e) {
                    log.error("doWork exception", e);
                }
            }
        }, "PIPELINE-THREAD");
    }

    @Override
    public void doWork() {
        consumer.subscribe(Collections.singletonList(this.topic));
        ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofSeconds(10));
        if (null == records || records.count() <= 0) {
            return;
        }
        for (final ConsumerRecord<Integer, String> record : records) {
            ///System.out.println("Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset());
            try {
                pipelineQueue.put(record);
                // 同步提交
                consumer.commitAsync();
            } catch (InterruptedException e) {
                log.error("doWork exception", e);
                continue;
            }
        }
    }

    private void processRecordItem(ConsumerRecord<Integer, String> record) {
        // 处理逻辑
    }

    @Override
    public String name() {
        return "PIPELINE-THREAD";
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }


    /**
     * 关闭钩子
     */
    @Override
    public void shutdown() {
        super.shutdown();
        executor.shutdown();
    }
}