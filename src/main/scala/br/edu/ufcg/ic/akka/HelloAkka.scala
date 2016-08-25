package br.edu.ufcg.ic.akka

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

/**
 * @author Adalberto
 */
class HelloAkka extends Actor{
  def receive = {
    case "hello" => println("hello back at you")
    case _       => println("huh?")
  }
}

class People extends Actor{  
  def receive = {
    case "hello" => println("hello back at you")
    case "whats your name?" => println("my name is ....")
    case _       => println("huh?")
  }
}

class FredActor(myName: String) extends Actor {
 def receive = {
   case "hello" => println("hello from %s".format(myName));
   case _ => println("'huh?', said %s".format(myName));
 }
}


object Main extends App {
  val system = ActorSystem("HelloSystem")
  // default Actor constructor
  val helloActor = system.actorOf(Props[HelloAkka], name = "helloactor")
  println("Here is helloactor talking....")
  helloActor ! "hello"
  helloActor ! "buenos dias"
  
  
  val marcos  = system.actorOf(Props[People], name = "people")
  println("Here is people talking....") 
  marcos ! "hello"
  marcos ! "whats your name?"
  
  val fredActor = system.actorOf(Props(new FredActor("Fred")), name = "fredactor")
  println("Here is fred talking....")
  fredActor ! "hello"
  fredActor ! "buenos dias"
  
  system.terminate()
}