package br.edu.ufcg.ic.akka.java;

import akka.actor.UntypedActor;
import akka.japi.Procedure;

public class HotSwapActor extends UntypedActor {
	
	Procedure<Object> angry = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
            if (message.equals("bar")) {
                getSender().tell("I am already angry?", getSelf());
            } else if (message.equals("foo")) {
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
                getContext().become(angry);
            }
        }
    };

    public void onReceive(Object message) {
        if (message.equals("bar")) {
            getContext().become(angry);
        } else if (message.equals("foo")) {
            getContext().become(happy);
        } else {
            unhandled(message);
        }
    }
    
    public static void main(String args[]) {
    	
    }

}
