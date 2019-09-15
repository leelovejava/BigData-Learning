package com.roncoo.es.senior;

import com.roncoo.es.senior.constant.EsConstant;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于地理位置对周围汽车4S店进行搜索
 *
 * @author leelovejava
 */
public class GeoLocationShopSearchApp {

    public static void main(String[] args) throws Exception {
        Settings settings = Settings.builder()
                .put("cluster.name", EsConstant.CLUSTER_NAME)
                .build();

        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName(EsConstant.ES_ADDRESS), 9300));

        SearchResponse searchResponse = client.prepareSearch("car_shop")
                .setTypes("shops")
                .setQuery(QueryBuilders.geoBoundingBoxQuery("pin.location")
                        .setCorners(40.73, -74.1, 40.01, -71.12))
                .get();

        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }

        System.out.println("====================================================");

        List<GeoPoint> points = new ArrayList<GeoPoint>();
        points.add(new GeoPoint(40.73, -74.1));
        points.add(new GeoPoint(40.01, -71.12));
        points.add(new GeoPoint(50.56, -90.58));

        searchResponse = client.prepareSearch("car_shop")
                .setTypes("shops")
                .setQuery(QueryBuilders.geoPolygonQuery("pin.location", points))
                .get();

        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }

        System.out.println("====================================================");

        searchResponse = client.prepareSearch("car_shop")
                .setTypes("shops")
                .setQuery(QueryBuilders.geoDistanceQuery("pin.location")
                        .point(40, -70)
                        .distance(200, DistanceUnit.KILOMETERS))
                .get();

        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }

        client.close();
    }

}
