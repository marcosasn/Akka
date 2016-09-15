package br.edu.ufcg.ic.akka

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorLogging

object PartialFunction extends App {
  
  // protocol
  case object GiveMeThings
  final case class Give(thing: Any) 
  final case class Input(number: Integer)
  case object Output
  
  trait ProducerBehavior {
    this: Actor =>
  
    val producerBehavior: Receive = {
        case GiveMeThings =>
            sender() ! Give("thing")
      }
  }

  
  trait ConsumerBehavior {
      this: Actor with ActorLogging =>
  
      val consumerBehavior: Receive = {
          case ref: ActorRef =>
              ref ! GiveMeThings
          case Give(thing) =>
              log.info("Got a thing! It's {}", thing)
  
      }
  }

  class Producer extends Actor with ProducerBehavior {
      def receive = producerBehavior
  }

  class Consumer extends Actor with ActorLogging with ConsumerBehavior {
      def receive = consumerBehavior
  }
  
  class Buffer extends Actor with ActorLogging {
      def receive = {
          case Input(number) =>
              //append number into list
          case Output =>
              //send number to ref request
      }
  }
}

