

def init(): String = {
  println("call init()")
  return ""
}


def noLazy() {
  //没有使用lazy修饰
  val property = init();
  println("after init()")
  println(property)
}

def lazyed() {
  //没有使用lazy修饰
  lazy val property = init();
  println("after init()")
  println(property)
}


noLazy()

lazyed()