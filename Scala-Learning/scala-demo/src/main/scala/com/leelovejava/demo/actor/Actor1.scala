import scala.actors.Actor

/**
  * 样例类
  * 将actor对象发送过去,已便通信
  *
  * @param actor
  * @param msg
  */
case class Message(actor: Actor, msg: Any)

/**
  * Actor与Actor之间通信
  */
class Actor1 extends Actor {
  def act() {
    while (true) {
      receive {
        case msg: Message => {
          println("i sava msg! = " + msg.msg)

          msg.actor ! "i love you too !"
        }
        case msg: String => println(msg)
        case _ => println("default msg!")
      }
    }
  }
}

class Actor2(actor: Actor) extends Actor {
  actor ! Message(this, "i love you !")

  def act() {
    while (true) {
      receive {
        case msg: String => {
          if (msg.equals("i love you too !")) {
            println(msg)
            actor ! "could we have a date !"
          }
        }
        case _ => println("default msg!")
      }
    }
  }
}

object Lesson_Actor2 {
  def main(args: Array[String]): Unit = {
    val actor1 = new Actor1()
    actor1.start()
    val actor2 = new Actor2(actor1)
    actor2.start()
  }
}