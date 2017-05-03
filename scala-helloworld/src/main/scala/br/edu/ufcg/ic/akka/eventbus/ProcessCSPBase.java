package br.edu.ufcg.ic.akka.eventbus;

import akka.actor.UntypedActor;

public abstract class ProcessCSPBase extends UntypedActor {
	protected enum State {
		started, stop, skip;
	}
	
	private State state;
	
	protected void init() {
		state = State.started;
	}

	protected void setState(State s) {
		if (state != s) {
			//transition(state, s);
			state = s;
		}
	}

	protected State getState() {
		return state;
	}

	abstract protected void transition(State old, Object event);	
	
	protected void syso(String msg){
		System.out.println(msg);
	}
}