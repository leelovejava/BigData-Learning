package com.leelovejava.demo.convert

class Animal(name:String){
  def canFly(): Unit ={
    println(s"$name can fly...")
  }
}
class Rabbit(xname:String){
  val name = xname
}

/**
  * 2.隐式转换函数
  * 隐式转换函数是使用关键字implicit修饰的方法。当Scala运行时，假设如果A类型变量调用了method()这个方法，发现A类型的变量没有method()方法，
  * 而B类型有此method()方法，会在作用域中寻找有没有隐式转换函数将A类型转换成B类型，如果有隐式转换函数，那么A类型就可以调用method()这个方法。
  * 隐式转换函数注意：隐式转换函数只与函数的参数类型和返回类型有关，与函数名称无关，所以作用域内不能有相同的参数类型和返回类型的不同名称隐式转换函数。
  */
object Lesson_ImplicitFunction {

  implicit def rabbitToAnimal(rabbit:Rabbit):Animal = {
      new Animal(rabbit.name)
  }

  def main(args: Array[String]): Unit = {
    val rabbit = new Rabbit("RABBIT")
    rabbit.canFly()
  }
}