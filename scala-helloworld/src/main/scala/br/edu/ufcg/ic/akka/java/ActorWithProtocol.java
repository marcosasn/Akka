package br.edu.ufcg.ic.akka.java;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorWithStash;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;

public class ActorWithProtocol extends UntypedActorWithStash  {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	public void onReceive(Object msg) {
        if (msg.equals("open")) {
			log.info("'open' message received... unstashAll()...");
            unstashAll();
            getContext().become(new Procedure<Object>() {
                public void apply(Object msg) throws Exception {
                    if (msg.equals("write")) {
                        // do writing...
            			log.info("'write' message received... ");

                    } else if (msg.equals("close")) {
            			log.info("'close' message received... unstashAll()...");
                        unstashAll(); //enfileira mensagens de mailbox de um ator
                        getContext().unbecome();
                    } else {
            			log.info("stash()" + msg);
                        stash();
                    }
                }
            }, false); // add behavior on top instead of replacing
        } else {
			log.info("stash()" + msg);
            stash();
        }
    }
	
	public static void main(String... args) {
        ActorSystem system = ActorSystem.create("MySystem");
	    ActorRef swap = system.actorOf(Props.create(ActorWithProtocol.class)); 
	    String[] msgs = new String[]{"file1", "file2", "file3", "write"};
	    
	    for(String msg: msgs){
	    	swap.tell(msg, ActorRef.noSender());
	    }
    	
    	msgs = new String[]{"open","close"};
    	
    	for(String msg: msgs){
	    	swap.tell(msg, ActorRef.noSender());
	    }
 	}
}