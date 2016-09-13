package br.edu.ufcg.ic.akka.java;

import static akka.pattern.Patterns.gracefulStop;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.pattern.AskTimeoutException;

class Cruncher extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void onReceive(Object message) throws Throwable {
		if (message.equals("crunch")) {
            log.info("Cruncher is crunching....");
        }else {
        	unhandled(message);
        }
	}
	
}

public class GracefulManager extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    public static final String SHUTDOWN = "shutdown";
    ActorRef worker = getContext().watch(getContext().actorOf(Props.create(Cruncher.class), "worker"));

    public void onReceive(Object message) {
        if (message.equals("job")) {
        	log.info("Cruncher will get job....");
            worker.tell("crunch", getSelf());
        } else if (message.equals(SHUTDOWN)) {
        	log.info("Cruncher will get die....");
        	worker.tell(PoisonPill.getInstance(), getSelf());
            getContext().become(shuttingDown);
        }
        else {
        	unhandled(message);
        }
    }

    Procedure<Object> shuttingDown = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
            if (message.equals("job")) {
            	log.info("Mannager is shutting down....");
                getSender().tell("service unavailable, shutting down", getSelf());
            } else if (message instanceof Terminated) {
                getContext().stop(getSelf());
            }
        }};

	public static void main (String args[]) {
		final ActorSystem system = ActorSystem.create("MySystem");
		ActorRef actorRef = system.actorOf(Props.create(GracefulManager.class),"actorref");;
		actorRef.tell("job", ActorRef.noSender());		
		Future<Boolean> stopped = gracefulStop(actorRef, Duration.create(5, TimeUnit.SECONDS), GracefulManager.SHUTDOWN);
		
		actorRef.tell("job", ActorRef.noSender());
		try {
			Await.result(stopped, Duration.create(6, TimeUnit.SECONDS));
			// the actor has been stopped
		} catch (Exception e) {
			e.printStackTrace();
			// the actor wasn't stopped within 5 seconds
		}
	}
}
