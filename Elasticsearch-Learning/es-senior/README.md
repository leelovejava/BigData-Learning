# ES汽车零售实战

## 课程大纲

快速入门篇，讲解过了一些基本的java api，包括了document增删改查，基本的搜索，基本的聚合

高手进阶篇，必须将java api这块深入讲解一下，介绍一些最常用的，最核心的一些api的使用，用一个模拟现实的案例背景，让大家在学习的时候更加贴近业务

话说在前面，我们是不可能将所有的java api用视频全部录制一遍的，因为api太多了。。。。

我们之前讲解各种功能，各种知识点，花了那么多的时间，哪儿些才是最最关键的，知识，原理，功能，es restful api，最次最次，哪怕是搞php，搞python的人也可以来学习

如果说，现在要将所有所有的api全部用java api实现一遍和讲解，太耗费时间了，几乎不可能接受

采取的粗略，将核心的java api语法，还有最最常用的那些api都给大家上课演示了

然后最后一讲，会告诉大家，在掌握了之前那些课程讲解的各种知识点之后，如果要用java api去实现和开发，应该怎么自己去探索和掌握

java api，api的学习，实际上是最最简单的，纯用，没什么难度，技术难度，你掌握了课上讲解的这些api之后，自己应该就可以举一反三，后面自己去探索和尝试出自己要用的各种功能对应的java api是什么。。。

## 1、client集群自动探查

默认情况下，是根据我们手动指定的所有节点，依次轮询这些节点，来发送各种请求的，如下面的代码，我们可以手动为client指定多个节点

TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost1"), 9300))
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost2"), 9300))
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost3"), 9300));

但是问题是，如果我们有成百上千个节点呢？难道也要这样手动添加吗？

es client提供了一种集群节点自动探查的功能，打开这个自动探查机制以后，es client会根据我们手动指定的几个节点连接过去，然后通过集群状态自动获取当前集群中的所有data node，然后用这份完整的列表更新自己内部要发送请求的node list。默认每隔5秒钟，就会更新一次node list。

但是注意，es cilent是不会将Master node纳入node list的，因为要避免给master node发送搜索等请求。

这样的话，我们其实直接就指定几个master node，或者1个node就好了，client会自动去探查集群的所有节点，而且每隔5秒还会自动刷新。非常棒。

Settings settings = Settings.builder()
        .put("client.transport.sniff", true).build();
TransportClient client = new PreBuiltTransportClient(settings);

使用上述的settings配置，将client.transport.sniff设置为true即可打开集群节点自动探查功能

在实际的生产环境中，都是这么玩儿的。。。

## 2、汽车零售案例背景

简单来说，会涉及到三个数据，汽车信息，汽车销售记录，汽车4S店信息

### 创建索引
settings：索引库的设置

- number_of_shards：分片数量
- number_of_replicas：副本数量

```shell script
PUT /car_shop
{
    "settings": {
        "number_of_shards": 1,
        "number_of_replicas": 1
      }
}
```

### 删除索引
```shell script
DELETE car_shop
```

### 创建映射
```shell script
PUT /索引库名/_mapping/类型名称
{
  "properties": {
    "字段名": {
      "type": "类型",
      "index": true,
      "store": true,
      "analyzer": "分词器"
    }
  }
}
```
类型名称：就是前面将的type的概念，类似于数据库中的不同表
字段名：任意填写	，可以指定许多属性，例如：

- type：类型，可以是text、long、short、date、integer、object等
- index：是否索引，默认为true
- store：是否存储，默认为false
- analyzer：分词器，这里的`ik_max_word`即使用ik分词器

```shell script
PUT car_shop/_mapping/cars
{
  "properties": {
    "brand": {
      "type": "text",
      "analyzer": "ik_max_word"
    },
    "name": {
      "type": "text",
      "analyzer": "ik_max_word"
    },
    "price": {
      "type": "float"
    },
    "produce_date": {
      "type": "date"
     }
  }
}
```

`BoolQuerySearchBrand`

```shell script
PUT /car_shop/cars/1
{
    "brand": "阿斯顿·马丁",
    "name": "V8 Vantage",
    "price": 2288000,
    "produce_date": "2011-10-01"
}

PUT /car_shop/cars/5
{
    "brand": "华晨宝马",
    "name": "宝马318",
    "price": 270000,
    "produce_date": "2017-01-20"
}
```

## 基于upsert实现汽车最新价格的调整

做一个汽车零售数据的mapping，我们要做的第一份数据，其实汽车信息
```shell script
PUT /car_shop
{
    "mappings": {
        "cars": {
            "properties": {
                "brand": {
                    "type": "text",
                    "analyzer": "ik_max_word",
                    "fields": {
                        "raw": {
                            "type": "keyword"
                        }
                    }
                },
                "name": {
                    "type": "text",
                    "analyzer": "ik_max_word",
                    "fields": {
                        "raw": {
                            "type": "keyword"
                        }
                    }
                }
            }
        }
    }
}
```

首先的话呢，第一次调整宝马320这个汽车的售价，我们希望将售价设置为32万，用一个upsert语法，如果这个汽车的信息之前不存在，那么就insert，如果存在，那么就update

IndexRequest indexRequest = new IndexRequest("car_shop", "cars", "1")
        .source(jsonBuilder()
            .startObject()
                .field("brand", "宝马")
                .field("name", "宝马320")
                .field("price", 320000)
                .field("produce_date", "2017-01-01")
            .endObject());

UpdateRequest updateRequest = new UpdateRequest("car_shop", "cars", "1")
        .doc(jsonBuilder()
            .startObject()
                .field("price", 320000)
            .endObject())
        .upsert(indexRequest);       
               
client.update(updateRequest).get();

IndexRequest indexRequest = new IndexRequest("car_shop", "cars", "1")
        .source(jsonBuilder()
            .startObject()
                .field("brand", "宝马")
                .field("name", "宝马320")
                .field("price", 310000)
                .field("produce_date", "2017-01-01")
            .endObject());
UpdateRequest updateRequest = new UpdateRequest("car_shop", "cars", "1")
        .doc(jsonBuilder()
            .startObject()
                .field("price", 310000)
            .endObject())
        .upsert(indexRequest);              
client.update(updateRequest).get();


## 基于search template实现按品牌分页查询模板

搜索模板的功能，java api怎么去调用一个搜索模板

```shell script
page_query_by_brand.mustache

{
  "from": {{from}},
  "size": {{size}},
  "query": {
    "match": {
      "brand.keyword": "{{brand}}" 
    }
  }
}
```

## 对汽车品牌进行全文检索、精准查询和前缀搜索
```shell script
PUT /car_shop/cars/5
{
        "brand": "华晨宝马",
        "name": "宝马318",
        "price": 270000,
        "produce_date": "2017-01-20"
}
```

```java
class FullTextSearchByBrand {
    public static void main(String[] args)  {
        SearchResponse response = client.prepareSearch("car_shop")
                .setTypes("cars")
                .setQuery(QueryBuilders.matchQuery("brand", "宝马"))                
                .get();
        
        SearchResponse response = client.prepareSearch("car_shop")
                .setTypes("cars")
                .setQuery(QueryBuilders.multiMatchQuery("宝马", "brand", "name"))                
                .get();
        
        SearchResponse response = client.prepareSearch("car_shop")
                .setTypes("cars")
                .setQuery(QueryBuilders.commonTermsQuery("name", "宝马320"))                
                .get();
        
        SearchResponse response = client.prepareSearch("car_shop")
                .setTypes("cars")
                .setQuery(QueryBuilders.prefixQuery("name", "宝"))                
                .get();
    }
}
```

## Java API_对汽车品牌进行多种条件的组合搜索
```java
public class BoolQuerySearchBrand {

    public static void main(String[] args) {
        QueryBuilder qb = boolQuery()
            .must(matchQuery("brand", "宝马"))    
            .mustNot(termQuery("name.raw", "宝马318")) 
            .should(termQuery("produce_date", "2017-01-02"))  
            .filter(rangeQuery("price").gte("280000").lt("350000"));
        
        SearchResponse response = client.prepareSearch("car_shop")
                .setTypes("cars")
                .setQuery(qb)                
                .get();
    }
}
```

## 基于地理位置对周围汽车4S店进行搜索
`GeoLocationShopSearchApp`
```
<dependency>
    <groupId>org.locationtech.spatial4j</groupId>
    <artifactId>spatial4j</artifactId>
    <version>0.6</version>                        
</dependency>

<dependency>
    <groupId>com.vividsolutions</groupId>
    <artifactId>jts</artifactId>
    <version>1.13</version>                         
    <exclusions>
        <exclusion>
            <groupId>xerces</groupId>
            <artifactId>xercesImpl</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```
比如我们有很多的4s店，然后呢给了用户一个app，在某个地方的时候，可以根据当前的地理位置搜索一下，自己附近的4s店
```
POST /car_shop/_mapping/shops
{
  "properties": {
      "pin": {
          "properties": {
              "location": {
                  "type": "geo_point"
              }
          }
      }
  }
}
```

```
PUT /car_shop/shops/1
{
    "name": "上海至全宝马4S店",
    "pin" : {
        "location" : {
            "lat" : 40.12,
            "lon" : -71.34
        }
    }
}
```

第一个需求：搜索两个坐标点组成的一个区域

> QueryBuilder qb = geoBoundingBoxQuery("pin.location").setCorners(40.73, -74.1, 40.01, -71.12); 

第二个需求：指定一个区域，由三个坐标点，组成，比如上海大厦，东方明珠塔，上海火车站
```
List<GeoPoint> points = new ArrayList<>();             
points.add(new GeoPoint(40.73, -74.1));
points.add(new GeoPoint(40.01, -71.12));
points.add(new GeoPoint(50.56, -90.58));

QueryBuilder qb = geoPolygonQuery("pin.location", points); 
```

第三个需求：搜索距离当前位置在200公里内的4s店
```
QueryBuilder qb = geoDistanceQuery("pin.location").point(40, -70).distance(200, DistanceUnit.KILOMETERS);   

SearchResponse response = client.prepareSearch("car_shop")
        .setTypes("shops")
        .setQuery(qb)                
        .get();
```

## 如何自己尝试API以掌握所有搜索和聚合的语法
1、自己要什么query，自己去用QueryBuilders去尝试，disMaxQuery
2、自己的某种query，有一些特殊的参数，tieBreaker，自己拿着query去尝试，点一下，看ide的自动提示，自己扫一眼，不就知道有哪些query，哪些参数了
3、如果不是query，是聚合，也是一样的，你就拿AggregationBuilders，点一下，看一下ide的自动提示，我们讲过的各种语法，都有了
4、包括各种聚合的参数，也是一样的，找到对应的AggregationBuilder之后，自己点一下，找需要的参数就可以了
5、自己不断尝试，就o了，组装了一个搜索，或者聚合，自己试一下，测一下
