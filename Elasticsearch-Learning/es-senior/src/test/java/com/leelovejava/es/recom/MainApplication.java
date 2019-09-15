package com.leelovejava.es.recom;


import com.google.gson.Gson;
import com.roncoo.es.senior.constant.EsConstant;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用scroll结合scan遍历所有数据
 */
public class MainApplication {
    private static int i = 0;
    private static ElasticsearchUtils esUtils;

    public static void main(String[] args) throws UnknownHostException {
        // 创建ElasticsearchUtils对象,设置集群名称,IP地址,端口号
        esUtils = new ElasticsearchUtils(EsConstant.CLUSTER_NAME, EsConstant.ES_ADDRESS, 9300);

        // 插入数据
        //insertData(10000);

        // 获取Client对象,设置索引名称,搜索类型(SearchType.SCAN),搜索数量,发送请求
        SearchResponse searchResponse = esUtils.getClient()
                .prepareSearch("school").setSearchType(SearchType.DEFAULT)
                .setSize(10).setScroll(new TimeValue(20000)).execute()
                // 注意:首次搜索并不包含数据
                .actionGet();
        // 获取总数量
        long totalCount = searchResponse.getHits().getTotalHits();
        // 计算总页数,每次搜索数量为分片数*设置的size大小
        int page = (int) totalCount / (5 * 10);
        System.out.println(totalCount);
        for (int i = 0; i <= page; i++) {
            // 再次发送请求,并使用上次搜索结果的ScrollId
            searchResponse = esUtils.getClient()
                    .prepareSearchScroll(searchResponse.getScrollId())
                    .setScroll(new TimeValue(20000)).execute()
                    .actionGet();
            parseSearchResponse(searchResponse);
        }

    }

    public static void parseSearchResponse(SearchResponse searchResponse) {
        SearchHits hits = searchResponse.getHits();
        System.out.println("-----------begin------------");
        for (SearchHit searchHit : hits.getHits()) {
            try {
                i++;
                String id = searchHit.getId();
                System.out.println("第" + i + "条数据:" + id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("-----------end------------");
    }

    private static AtomicInteger esNum = new AtomicInteger(0);

    /**
     * 创造假数据
     *
     * @param count 数据量
     */
    public static void insertData(int count) {
        System.out.println("-----------begin------------");


        for (int i = 1; i <= count; i++) {
            new Thread(() -> {
                int num = esNum.addAndGet(1);

                Student student = new Student();
                student.setName("name" + num);
                student.setId(num);
                // 设置存入ES中的ID
                String id = "id_" + num;
                // 插入数据(数据量大时,最好使用批量插入,此处为单条插入)
                esUtils.insert("school", "student", id, new Gson().toJson(student));
                System.out.println("完成第" + (num + 1) + "条数据");
            }).start();
        }

        System.out.println("-----------end------------");
    }


}