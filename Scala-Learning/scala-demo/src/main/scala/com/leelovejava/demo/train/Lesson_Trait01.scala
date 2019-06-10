package com.leelovejava.demo.train

/**
  * trait中带属性带方法实现
  * trait 特质特性，第一个关键字用extends，之后用with
  * 继承的多个trait中如果有同名的方法和属性，必须要在类中使用“override”重新定义。
  * trait中不可以传参数
  */
trait Speak {
  val gender = "m"

  def speak(name: String) = {
    println(name + " is speaking...")
  }
}

trait Listen {
  val gender = "m"

  def listen(name: String) = {
    println(name + " is listening...")
  }
}

class Person1() extends Speak with Listen {
  override val gender = "f"
}


object Lesson_Trait01 {
  def main(args: Array[String]): Unit = {
    val p = new Person1()
    p.speak("zhangsan")
    p.listen("lisi")
    println("gender:" + p.gender)
  }
}