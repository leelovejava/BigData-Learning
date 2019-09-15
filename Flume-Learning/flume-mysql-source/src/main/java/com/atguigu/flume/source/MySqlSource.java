package com.atguigu.flume.source;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.source.AbstractSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author leelovejava
 */
public class MySqlSource extends AbstractSource implements Configurable, PollableSource {

    /**
     * 打印日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(MySqlSource.class);

    /**
     * 定义sqlHelper
     */
    private MySqlSourceHelper mySqlSourceHelper;


    @Override
    public long getBackOffSleepIncrement() {
        return 0;
    }

    @Override
    public long getMaxBackOffSleepInterval() {
        return 0;
    }

    @Override
    public void configure(Context context) {

        try {
            // 初始化
            mySqlSourceHelper = new MySqlSourceHelper(context);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据
     *
     * @return
     * @throws EventDeliveryException
     */
    @Override
    public Status process() throws EventDeliveryException {

        try {
            // 查询数据表
            List<List<Object>> result = mySqlSourceHelper.executeQuery();

            // 存放event的集合
            List<Event> events = new ArrayList<>(10);

            // 存放event头集合
            HashMap<String, String> header = new HashMap<>(10);

            // 如果有返回数据，则将数据封装为event
            if (!result.isEmpty()) {

                List<String> allRows = mySqlSourceHelper.getAllRows(result);

                Event event;

                for (String row : allRows) {
                    event = new SimpleEvent();
                    event.setBody(row.getBytes());
                    event.setHeaders(header);
                    events.add(event);
                }

                // 将event写入channel
                this.getChannelProcessor().processEventBatch(events);

                // 更新数据表中的offset信息
                mySqlSourceHelper.updateOffset2DB(result.size());
            }

            // 等待时长
            Thread.sleep(mySqlSourceHelper.getRunQueryDelay());

            return Status.READY;
        } catch (InterruptedException e) {
            LOG.error("Error procesing row", e);

            return Status.BACKOFF;
        }
    }

    @Override
    public synchronized void stop() {

        LOG.info("Stopping sql source {} ...", getName());

        try {
            // 关闭资源
            mySqlSourceHelper.close();
        } finally {
            super.stop();
        }
    }
}