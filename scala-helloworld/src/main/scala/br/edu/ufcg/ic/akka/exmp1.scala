package br.edu.ufcg.ic.akka

import akka.actor.{ ActorRef, ActorSystem, PoisonPill, Terminated, ActorLogging }
import akka.actor._
import akka.actor.ActorSystem
import akka.dispatch.Foreach
import akka.actor.Inbox
import akka.actor.ActorLogging
import akka.event.Logging

//#my-actor
class MyActor extends Actor with ActorLogging{
  //val log = Logging(context.system, this)
  //https://github.com/akka/akka/blob/v2.4.9-RC2/akka-docs/rst/scala/code/docs/actor/ActorDocSpec.scala
  def receive = {
    case "test" => log.info("received test")
    case _      => log.info("received unknown message")
  }
}

//final case class DoIt(msg: ImmutableMessage)
final case class Message(s: String)

//#context-actorOf
class FirstActor extends Actor {
  val child = context.actorOf(Props[MyActor], name = "myChild")
  //#plus-some-behavior
  def receive = {
    case x => sender() ! x
  }
  //#plus-some-behavior
}


object MyActor {
    case class Greeting(from: String)
    case object Goodbye
}

class MyActor1 extends Actor with ActorLogging {
    import MyActor._
    
    def receive = {
        case Greeting(greeter) => 
          log.info(s"I was greeted by $greeter.")
        case Goodbye => 
          log.info("Someone said goodbye to me.")
    }
}

object MyActorMain extends App {
  
  // ActorSystem is a heavy object: create only one per application
  val system = ActorSystem("mySystem")
  val myActor = system.actorOf(Props[MyActor], "myactor2")
  
  implicit val i = Inbox.create(system)
  i watch myActor
  myActor ! MyActor.Goodbye
  
  //i.receive() should ===("hello") 
}