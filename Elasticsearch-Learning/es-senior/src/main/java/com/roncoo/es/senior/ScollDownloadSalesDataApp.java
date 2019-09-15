package com.roncoo.es.senior;

import com.roncoo.es.senior.constant.EsConstant;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;

/**
 * 基于scroll实现月度销售数据批量下载
 * @author leelovejava
 */
public class ScollDownloadSalesDataApp {

    public static void main(String[] args) throws Exception {
        Settings settings = Settings.builder()
                .put("cluster.name", EsConstant.CLUSTER_NAME)
                .build();

        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName(EsConstant.ES_ADDRESS), 9300));

        SearchResponse searchResponse = client.prepareSearch("car_shop")
                .setTypes("sales")
                .setQuery(QueryBuilders.termQuery("brand.keyword", "宝马"))
                .setScroll(new TimeValue(60000))
                .setSize(1)
                .get();

        int batchCount = 0;

        do {
            for (SearchHit searchHit : searchResponse.getHits().getHits()) {
                System.out.println("batch: " + ++batchCount);
                System.out.println(searchHit.getSourceAsString());

                // 每次查询一批数据，比如1000行，然后写入本地的一个excel文件中

                // 如果说你一下子查询几十万条数据，不现实，jvm内存可能都会爆掉
            }

            searchResponse = client.prepareSearchScroll(searchResponse.getScrollId())
                    .setScroll(new TimeValue(60000))
                    .execute()
                    .actionGet();
        } while (searchResponse.getHits().getHits().length != 0);

        client.close();
    }

}
