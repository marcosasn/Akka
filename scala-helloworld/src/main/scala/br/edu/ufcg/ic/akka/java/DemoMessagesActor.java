package br.edu.ufcg.ic.akka.java;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class DemoMessagesActor extends UntypedActor {
    
	static public class Greeting {
        private final String from;
        
        public Greeting(String from) {
            this.from = from;
        }

        public String getGreeter() {
    		return from;
        }
    }
	
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public void onReceive(Object message) throws Exception {
        if (message instanceof Greeting) {
            log.info("Received message: {}", ((Greeting) message).getGreeter());
            log.info("Sending message: {}", message);
            getSender().tell(message, getSelf());
        }        
        else unhandled(message);
    }
    
    public static void main(String args[]) {
		final ActorSystem system = ActorSystem.create("MySystem");
		final ActorRef dma = system.actorOf(Props.create(DemoMessagesActor.class),"dma");	
		final ActorRef odma = system.actorOf(Props.create(DemoMessagesActor.class),"odma");
		Greeting g = new DemoMessagesActor.Greeting("OtherDemoMessagesActor");
		dma.tell(g, ActorRef.noSender());
		
		/*dma.tell(PoisonPill.getInstance(), ActorRef.noSender());
		odma.tell(PoisonPill.getInstance(), ActorRef.noSender());
		system.shutdown();*/
	}
}
