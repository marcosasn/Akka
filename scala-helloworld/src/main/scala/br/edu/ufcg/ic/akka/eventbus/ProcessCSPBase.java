package br.edu.ufcg.ic.akka.eventbus;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.japi.Procedure;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.InterState;;

public abstract class ProcessCSPBase extends UntypedActor {
	protected static enum State {
		started, deadlock, sucess;
	}
	
	ActorRef requesterSkipRef;
	private List<String> initials;	
	Procedure<Object> nextBehavior;
	private State state;
	
	Procedure<Object> skip = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
        	if(getState() == State.sucess){
        		requesterSkipRef.tell("Sucess", getSelf());
        		setState(State.deadlock);
        		getContext().become(deadlock);
        	}
        }
    };
	
    Procedure<Object> deadlock = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
        	if(getState() == State.deadlock){
        		syso(getSelf().path().name().toString() + " is deadlock");
        		getSender().tell(new InterState(getState()), getSelf());
        	}
        }
    };
	
	protected void initialize() {
		state = State.started;
		initials = new ArrayList<String>();
		initials.add("a");
	}

	protected void setState(State s) {
		if (state != s) {
			state = s;
		}
	}

	protected State getState() {
		return state;
	}
	
	protected List<String> getInitials() {
		return initials;
	}

	protected void syso(String msg){
		System.out.println(msg);
	}
	
	abstract protected void transition(State old, String event);	
	
	abstract protected void peform(String event);	

}