package br.edu.ufcg.ic.akka.java;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorWithStash;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;

public class InitializeActor extends UntypedActorWithStash{
	private String initializeMe = null;
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void onReceive(Object message) {
	    if (message.equals("init")) {
	        initializeMe = "Up and running";
			log.info("I got init...");

	        getContext().become(new Procedure<Object>() {

				@Override
	            public void apply(Object message) throws Exception {
	                if (message.equals("U OK?"))
	        			log.info(initializeMe);
	                    getSender().tell(initializeMe, getSelf());
	                }
	            });
	    }
	    else {
	    	log.info((String) message);
	    	stash();
	    }
	}
	
	public static void main (String[] args) {
		final ActorSystem system = ActorSystem.create("MySystem");
		final ActorRef i = system.actorOf(Props.create(InitializeActor.class),"i");
		final ActorRef j = system.actorOf(Props.create(InitializeActor.class),"j");

		i.tell("init",j);
		i.tell("U OK?",j);

		
	}
}
