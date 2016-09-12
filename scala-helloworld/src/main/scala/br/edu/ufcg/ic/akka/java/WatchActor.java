package br.edu.ufcg.ic.akka.java;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class WatchActor extends UntypedActor {
    final ActorRef child = this.getContext().actorOf(Props.empty(), "child");
    {
    this.getContext().watch(child); // <-- the only call needed for registration
     }
    ActorRef lastSender = getContext().system().deadLetters();
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public void onReceive(Object message) {
        if (message.equals("kill")) {
            getContext().stop(child);
            lastSender = getSender();
            log.info("Received String message: {}", message);
            log.info("From: {}", lastSender);

        } else if (message instanceof Terminated) {
            final Terminated t = (Terminated) message;
            if (t.getActor() == child) {
                lastSender.tell("finished", getSelf());
                log.info("Filho finalizado");
            }
         } else if (message.equals("finished")){
        	 log.info("Confirmação de finalização.");
         } else
        	 unhandled(message);
    }
    
    public static void main(String args[]) {
		final ActorSystem system = ActorSystem.create("MySystem");
		final ActorRef dma = system.actorOf(Props.create(WatchActor.class),"dma");
		final ActorRef odma = system.actorOf(Props.create(WatchActor.class),"odma");
		dma.tell("kill", odma);
		
		dma.tell(PoisonPill.getInstance(), ActorRef.noSender());
		odma.tell(PoisonPill.getInstance(), ActorRef.noSender());
		system.shutdown();
	}
}
