package com.leelovejava.es.recom;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class ElasticsearchUtils {

    private TransportClient client;

    public ElasticsearchUtils(String clusterName, String ipAddress, int port) throws UnknownHostException {
        Settings settings = Settings.builder()
                // 为true忽略集群名验证
                //.put("client.transport.ignore_cluster_name", false)
                .put("cluster.name", clusterName)
                // 探测集群中机器状态
                .put("client.transport.sniff", false)
                .build();

        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName(ipAddress), port));
    }

    public Client getClient() {
        return this.client;
    }


    /**
     * 创建索引
     *
     * @param indexName 索引名称，相当于数据库名称
     * @param typeName  索引类型，相当于数据库中的表名
     * @param id        id名称，相当于每个表中某一行记录的标识
     * @param jsonData  json数据
     */
    public void insert(String indexName, String typeName, String id,
                       String jsonData) {
        //IndexRequestBuilder requestBuilder = client.prepareIndex(indexName,
        //        typeName, id).setRefresh(true);//设置索引名称，索引类型，id
        //requestBuilder.setSource(jsonData).execute().actionGet();//创建索引

        IndexResponse response = client.prepareIndex(indexName, typeName)
                .setSource(jsonData, XContentType.JSON)
                .get();
        //this.client.admin().indices().prepareCreate(indexName).get();
    }
}