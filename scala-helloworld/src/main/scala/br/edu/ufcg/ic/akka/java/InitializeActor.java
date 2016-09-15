package br.edu.ufcg.ic.akka.java;

import akka.actor.UntypedActor;
import akka.japi.Procedure;

public class InitializeActor extends UntypedActor{
	private String initializeMe = null;

	@Override
	public void onReceive(Object message) {
	    if (message.equals("init")) {
	        initializeMe = "Up and running";
	        getContext().become(new Procedure<Object>() {

				@Override
	            public void apply(Object message) throws Exception {
	                if (message.equals("U OK?"))
	                    getSender().tell(initializeMe, getSelf());
	                }
	            });
	    }
	    else {
	    	//stash();
	    }
	}
}
