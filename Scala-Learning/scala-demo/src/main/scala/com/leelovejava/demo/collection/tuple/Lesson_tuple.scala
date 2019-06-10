package com.leelovejava.demo.collection.tuple

object Lesson_tuple {
  def main(args: Array[String]): Unit = {
    //创建，最多支持22个

    // val  tuple = new Tuple（1） 可以使用new
    val tuple = new Tuple1(1)
    // val tuple2  = Tuple（1,2） 可以不使用new，也可以直接写成val tuple3 =（1,2,3）
    val tuple2 = Tuple2("zhangsan", 2)

    val tuple3 = Tuple3(1, 2, 3)
    val tuple4 = (1, 2, 3, 4)
    val tuple18 = Tuple18(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)
    val tuple22 = new Tuple22(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22)

    //使用
    println(tuple2._1 + "\t" + tuple2._2)
    val t = Tuple2((1, 2), ("zhangsan", "lisi"))
    println(t._1._2)


    /**
      * 3. 遍历
      */
    // tuple.productIterator得到迭代器，进而遍历
    val tupleIterator = tuple22.productIterator
    while (tupleIterator.hasNext) {
      println(tupleIterator.next())
    }


    /**
      * 4. 方法
      */
    // 4.1 翻转，只针对二元组
    println("******method***********")
    // (zhangsan,2)
    println(tuple2)
    // (2,zhangsan)
    println(tuple2.swap)

    // 4.2 toString
    println(tuple3.toString())

    println("******method***********")
  }
}
