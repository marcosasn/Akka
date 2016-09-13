package br.edu.ufcg.ic.akka.java;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;

public class MyReceiveTimeoutUntypedActor extends UntypedActor {
	
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	public MyReceiveTimeoutUntypedActor() {
		// To set an initial delay
		getContext().setReceiveTimeout(Duration.create("30 seconds"));
	}

	public void onReceive(Object message) {
		if (message instanceof String){
			if (message.equals("Hello")) {
				// To set in a response to a message
	            log.info("Sending response to 'Hello'....");
				getSender().tell("Word!", getSelf());
				getContext().setReceiveTimeout(Duration.create("1 second"));
			} else if (message.equals("Word!")) {
	            log.info("Getting response to 'Hello'....");
			}
		} else if (message instanceof ReceiveTimeout) {
			// To turn it off
			getContext().setReceiveTimeout(Duration.Undefined());
		} else {
			unhandled(message);
		}
	}
	
	public static void main(String args[]) {
		final ActorSystem system = ActorSystem.create("MySystem");
		final ActorRef myActor = system.actorOf(Props.create(MyReceiveTimeoutUntypedActor.class),"myactor");
		final ActorRef myActor2 = system.actorOf(Props.create(MyReceiveTimeoutUntypedActor.class),"myactor2");
		
		myActor.tell("Hello", myActor2);
	}
}
