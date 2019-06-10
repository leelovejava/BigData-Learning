package com.leelovejava.demo.collection.map

object Lesson_map {
  def main(args: Array[String]): Unit = {
    /**
      * 1.创建map
      */
    val map = Map(
      "1" -> "bjsxt",
      2 -> "shsxt",
      (3, "xasxt")
    )

    /**
      * 2.获取map的值
      */
    //获取值
    println(map.get("1").get)
    // 如果map中没有对应项，赋值为getOrElse传的值
    val result = map.get(8).getOrElse("no value")
    println(result)

    /**
      * 3.遍历map
      */
    // 3.1 遍历key和value
    for (x <- map) {
      println("====key:" + x._1 + ",value:" + x._2)
    }
    map.foreach(f => {
      println("key:" + f._1 + " ,value:" + f._2)
    })

    // 3.2 遍历key
    val keyIterable = map.keys
    keyIterable.foreach { key => {
      println("key:" + key + ", value:" + map.get(key).get)
    }
    }
    println("---------")


    // 3.3 遍历value
    val valueIterable = map.values
    valueIterable.foreach { value => {
      println("value: " + value)
    }
    }

    /**
      * 4. 合并map
      * ++  例：map1.++(map2)  --map1中加入map2
      * ++:  例：map1.++:(map2) –map2中加入map1
      * 注意：合并map会将map中的相同key的value替换
      */
    val map1 = Map(
      (1, "a"),
      (2, "b"),
      (3, "c")
    )
    val map2 = Map(
      (1, "aa"),
      (2, "bb"),
      (2, 90),
      (4, 22),
      (4, "dd")
    )
    map1.++:(map2).foreach(println)


    /**
      * 5. map方法
      */
    //count: 统计符合条件的记录数
    val countResult = map.count(p => {
      p._2.equals("shsxt")
    })
    println(countResult)

    //filter: 过滤，留下符合条件的记录
    map.filter(_._2.equals("shsxt")).foreach(println)

    //contains: map中是否包含某个key
    println(map.contains(2))

    //exist: 符合条件的记录存在不存在
    println(map.exists(f => {
      f._2.equals("xasxt")

    }))

    // mkString: 集合所有元素作为字符串显示
    println(map.mkString)

    // take: 返回前 n 个元素
    println(map.take(1))

    // 集合转数组
    println(map.toArray)
  }
}
