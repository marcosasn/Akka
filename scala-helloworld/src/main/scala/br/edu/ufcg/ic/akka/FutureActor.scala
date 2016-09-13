package br.edu.ufcg.ic.akka

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.{ ask, pipe }
import scala.concurrent.Future;
import akka.util.Timeout;
import scala.concurrent.duration.FiniteDuration

object FutureActor extends App {

  future()

  final case class Result(x: Int, s: String, d: Double)
  case object Request
  
  class FutureActor extends Actor {  
        
    def receive = {
      case Request =>
        println("\n\t receive requeste from: \t\t%s\n\t".format(sender()))
        try {
          val result = Request
          sender() ! result
        } catch {
          case e: Exception =>
            sender() ! akka.actor.Status.Failure(e)
            throw e
        }

      case Result(x, s, d) =>
        println("\n\t receive result from: \t\t%s\n\t".format(sender()))
    }
  }

  def future(){
    val system = ActorSystem("system")
    import system.dispatcher // The ExecutionContext that will be used
    implicit val timeout = Timeout(FiniteDuration(5, "seconds")) // needed for `?` below
    
    val actorA = system.actorOf(Props[FutureActor], "actora")
    val actorB = system.actorOf(Props[FutureActor], "actorb")
    val actorC = system.actorOf(Props[FutureActor], "actorc")
    val actorD = system.actorOf(Props[FutureActor], "actord")

    val f: Future[Result] =
        for {
            x <- ask(actorA, Request).mapTo[Int] // call pattern directly
            s <- (actorB ask Request).mapTo[String] // call by implicit conversion
            d <- (actorC ? Request).mapTo[Double] // call by symbolic name
        } yield Result(x, s, d)
    
    //f pipeTo actorD .. or .. pipe(f) to actorD
    pipe(f) to actorD
    println(f.foreach(println))
  }
}

