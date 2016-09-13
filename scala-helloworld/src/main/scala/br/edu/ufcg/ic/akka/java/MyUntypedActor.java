package br.edu.ufcg.ic.akka.java;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorKilledException;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Kill;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorContext;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import scala.concurrent.duration.Duration;

public class MyUntypedActor extends UntypedActor {
	
	public static Props props() {
        return Props.create(new Creator<MyUntypedActor>() {
            private static final long serialVersionUID = 1L;

            @Override
            public MyUntypedActor create() throws Exception {
                return new MyUntypedActor();
            }

        });
    }
	
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    final ActorRef child = getContext().actorOf(Props.create(MyUntypedActor.class), "myChild");
    
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            log.info("Received String message: {}", message);
            getSender().tell(message, getSelf());
        } else 
        	unhandled(message);
    }
    
    @Override
    public UntypedActorContext getContext() {
    	return super.getContext();
    }

    public static void main(String args[]) {
    	// Creating Actor using Props...
    	Props props1 = Props.create(MyUntypedActor.class);
		//Props props2 = Props.create(MyUntypedActor.class, "...");//actor with arguments
		Props props3 = Props.create(new MyUntypedActorC());//using Creator

		final ActorSystem system = ActorSystem.create("MySystem");
		final Inbox inbox = Inbox.create(system);
		final ActorRef myActor = system.actorOf(Props.create(MyUntypedActor.class),"myactor");
		final ActorRef myActor2 = system.actorOf(Props.create(MyUntypedActor.class),"myactor2");
    	final ActorRef target = myActor2;
		
    	inbox.send(target, "hello");

    	try {
    	    assert inbox.receive(Duration.create(1, TimeUnit.SECONDS)).equals("hello");
    	    System.out.println("try 1: " + inbox.receive(Duration.create(1, TimeUnit.SECONDS)).equals("hello"));
    	} catch (java.util.concurrent.TimeoutException e) {
    	    // timeout
    	}
    	
    	final ActorRef target2 = system.actorOf(Props.create(MyUntypedActor.class),"target2");
    	final Inbox inbox2 = Inbox.create(system);
    	inbox2.watch(target2);
    	target2.tell(PoisonPill.getInstance(), ActorRef.noSender());

    	try {
    	    assert inbox2.receive(Duration.create(1, TimeUnit.SECONDS)) instanceof Terminated;
    	    System.out.println(inbox2.receive(Duration.create(1, TimeUnit.SECONDS)) instanceof Terminated);
    	} catch (java.util.concurrent.TimeoutException e) {
    	    System.out.println("entrou aqui....." + e.getMessage());
    	}

		
		// ActorSystem is a heavy object: create only one per application
    	//myActor.tell("Hi",ActorRef.noSender());   
    	
    	myActor.tell(Kill.getInstance(), ActorRef.noSender());
    		
    	/*system.stop(myActor2);
    	system.stop(target);
    	system.stop(target2);
    	system.shutdown();*/
    }
}

@SuppressWarnings("serial")
final class MyUntypedActorC implements Creator<MyUntypedActor> {
	@Override
    public MyUntypedActor create() {
         return new MyUntypedActor();
    }
}

@SuppressWarnings("serial")
final class ParametricCreator<T extends MyUntypedActor> implements Creator<T> {
    @SuppressWarnings("unchecked")
	@Override 
     public T create() {
    	return (T) new MyUntypedActor();
    }
}
