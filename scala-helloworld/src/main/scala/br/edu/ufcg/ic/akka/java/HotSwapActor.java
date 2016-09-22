package br.edu.ufcg.ic.akka.java;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;

public class HotSwapActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	Procedure<Object> angry = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
            if (message.equals("bar")) {
                getSender().tell("I am already angry?", getSelf());
            } else if (message.equals("foo")) {
            	log.info("getting happy...");
                getContext().become(happy);
            }
        }
    };

    Procedure<Object> happy = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
            if (message.equals("bar")) {
                getSender().tell("I am already happy :-)", getSelf());
            } else if (message.equals("foo")) {
    			log.info("getting angry...");
                getContext().become(angry);
            }
        }
    };

    public void onReceive(Object message) {
        if (message.equals("bar")) {
			log.info("getting angry...");
            getContext().become(angry);
        } else if (message.equals("foo")) {
        	log.info("getting happy...");
        	getContext().become(happy);
        } else {
        	log.info((String) message);
            unhandled(message);
        }
    }
    
    public static void main(String args[]) {
		final ActorSystem system = ActorSystem.create("MySystem");
		final ActorRef actor1 = system.actorOf(Props.create(HotSwapActor.class),"actor1");
		final ActorRef actor2 = system.actorOf(Props.create(HotSwapActor.class),"actor2");

		actor1.tell("bar",actor2);
		actor1.tell("bar",actor2);
		actor1.tell("foo",actor2);
		actor1.tell("bar",actor2);
		actor1.tell("foo",actor2);
    }
}
