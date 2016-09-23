package br.edu.ufcg.ic.akka.java;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.Creator;

public class DemoActor extends UntypedActor{
	
    /**
    * Create Props for an actor of this type.
    * @param magicNumber The magic number to be passed to this actor’s constructor. 
    * @return a Props for creating this actor, which can then be further configured
    *  (e.g. calling `.withDispatcher()` on it)
    */
    public static Props props(final int magicNumber) {
        return Props.create(new Creator<DemoActor>() {
            private static final long serialVersionUID = 1L;

            @Override
            public DemoActor create() throws Exception {
                return new DemoActor(magicNumber);
            }

        });
    }
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    final int magicNumber;
     
    public DemoActor(int magicNumber) {
    	this.magicNumber = magicNumber;
    }

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof String) {
			log.info("Hi, I am ", getSelf().toString());
			log.info("Received String message: {}", msg);
			log.info("From: ", getSender().toString());
			getSender().tell(msg, getSelf());
        } else 
        	unhandled(msg);
	}
	
	public static void main(String args[]) {
		final ActorSystem system = ActorSystem.create("MySystem");
		final ActorRef demoActor = system.actorOf(Props.create(DemoActor.class, 2),"demoactor");	
		final ActorRef otherDemoActor = system.actorOf(Props.create(DemoActor.class, 2),"otherDemoActor");
		demoActor.tell("Hi, I am otherdemoactor and you?", ActorRef.noSender());
	}
}
