package com.leelovejava.demo.collection

/**
  * map
  * 1.map创建
  * Map（1 –>”bjsxt’）
  * Map((1,”bjsxt”))
  * 注意：创建map时，相同的key被后面的相同的key顶替掉，只保留一个
  * 2.获取map的值
  * map.get(“1”).get
  * map.get(100).getOrElse(“no value”)：如果map中没有对应项，赋值为getOrElse传的值。
  *
  * 3.遍历map
  * for,foreach
  * 4.遍历key
  * map.keys
  *
  * 5.遍历value
  * map.values
  * 6.合并map
  * ++  例：map1.++(map2)  --map1中加入map2
  * ++:  例：map1.++:(map2) –map2中加入map1
  * 注意：合并map会将map中的相同key的value替换
  *
  * 7.map中的方法举例
  * filter:过滤，留下符合条件的记录
  * count:统计符合条件的记录数
  * contains：map中是否包含某个key
  * exist：符合条件的记录存在不存在
  */
package object map {

}
