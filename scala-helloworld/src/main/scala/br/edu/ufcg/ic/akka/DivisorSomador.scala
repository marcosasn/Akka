package br.edu.ufcg.ic.akka

import akka.actor._
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.RoundRobinPool

/**
 * @author Marcos
 */

object divisorSomador extends App {
  
  calculate(nrOfSomadores = 2, nrOfMessages = 2)

  sealed trait somadorMessage
  case object Dividir extends somadorMessage
  case class Somar(array: List[Int]) extends somadorMessage
  case class Resultado(value: Int) extends somadorMessage
  case class valorsoma(soma: Int)

  class Divisor(nrOfSomadores: Int, nrOfMessages: Int, listener: ActorRef, array: List[Int]) extends Actor {  
    var soma: Int = _
    var nrOfResults: Int = _
    val router = context.actorOf(Props[Somador].withRouter(
        RoundRobinPool(nrOfSomadores)), name = "router")
    
    def receive = {
      case Dividir =>
        for (i <- 0 until nrOfMessages) 
          router ! Somar(array)
      case Resultado(value) =>        
        soma += value
        nrOfResults += 1
        if (nrOfResults == nrOfMessages) {
          listener ! valorsoma(soma)
          context.stop(self)
        }
    }
  }

  class Listener extends Actor {
    def receive = {
      case valorsoma(soma) =>
        println("\n\t valor soma: \t\t%s\n\t".format(soma))
        context.system.shutdown()
    }
  }

  class Somador extends Actor {
    def receive = {
      case Somar(array) => 
        var value: Int = 0;
        for (i <- 0 until (array.size)){
          value += array(i);
        }
        sender ! Resultado(value);
    }
  }

  def calculate(nrOfSomadores: Int, nrOfMessages: Int) {
    val system = ActorSystem("DivisorSomadorSystem")

    val listener = system.actorOf(Props[Listener], name = "listener")
    val array: List[Int] = List(1, 2, 3)

    val divisor = system.actorOf(Props(
        new Divisor(nrOfSomadores, nrOfMessages, listener, array)),
      name = "divisor")
      
    divisor ! Dividir
    
    def foo(x : Array[String]) = x.foldLeft("")((a,b) => a + b)
    
    println(foo(Array("a","b","c")));
  }
}