package br.edu.ufcg.ic.akka.eventbus;

import java.util.List;

import akka.actor.UntypedActor;
import akka.japi.Procedure;

public abstract class ProcessCSPBase extends UntypedActor {
	protected static enum State {
		started, deadlock, executing;
	}
	
	private State state;
	
    Procedure<Object> deadlock = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
        	if(getState() == State.deadlock){}
        }
    };
	
	protected void initialize() {
		state = State.started;
	}

	protected void setState(State s) {
		if (state != s) {
			state = s;
		}
	}

	protected State getState() {
		return state;
	}
	
	protected void peform(Event event){
		getSelf().tell(event, getSelf());
	}
	
	protected abstract List<Event> initials() ;
		
	abstract protected void transition(State old, Event event);
	
	protected void syso(String msg){
		System.out.println(msg);
	}
}