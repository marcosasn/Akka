package br.edu.ufcg.ic.akka.java;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorWithStash;
import akka.japi.Procedure;

public class ActorWithProtocol extends UntypedActorWithStash  {
	
	public void onReceive(Object msg) {
        if (msg.equals("open")) {
            unstashAll();
            getContext().become(new Procedure<Object>() {
                public void apply(Object msg) throws Exception {
                    if (msg.equals("write")) {
                        // do writing...
                    } else if (msg.equals("close")) {
                        unstashAll(); //enfileira mensagens de mailbox de um ator
                        getContext().unbecome();
                    } else {
                        stash();
                    }
                }
            }, false); // add behavior on top instead of replacing
        } else {
            stash();
        }
    }
	
	public static void main(String... args) {
        ActorSystem system = ActorSystem.create("MySystem");
	    ActorRef swap = system.actorOf(Props.create(ActorWithProtocol.class));   
 	}
}