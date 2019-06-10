package com.leelovejava.demo.convert

/**
  * 1. 隐式值与隐式参数
  */
object Lesson_ImplicitValue {
  /**
    * 隐式值是指在定义参数时前面加上implicit。隐式参数是指在定义方法时，方法中的部分参数是由implicit修饰【必须使用柯里化的方式，将隐式参数写在后面的括号中】。
    * 隐式转换作用就是：当调用方法时，不必手动传入方法中的隐式参数，Scala会自动在作用域范围内寻找隐式值自动传入。
    * 隐式值和隐式参数注意：
    * 1). 同类型的参数的隐式值只能在作用域内出现一次，同一个作用域内不能定义多个类型一样的隐式值。
    * 2). implicit 关键字必须放在隐式参数定义的开头
    * 3). 一个方法只有一个参数是隐式转换参数时，那么可以直接定义implicit关键字修饰的参数，调用时直接创建类型不传入参数即可。
    * 4). 一个方法如果有多个参数，要实现部分参数的隐式转换,必须使用柯里化这种方式,隐式关键字出现在后面，只能出现一次
    *
    * @param age
    * @param name
    * @param i
    */
  def Student(age: Int)(implicit name: String, i: Int) = {
    println(s"student :$name ,age = $age ,score = $i")
  }

  def Teacher(implicit name: String) = {
    println(s"teacher name is = $name")
  }

  def main(args: Array[String]): Unit = {
    // 同一个作用域内不能定义多个类型一样的隐式值
    implicit val zs = "zhangsan"
    implicit val sr = 100

    Student(18)
    Teacher
  }
}