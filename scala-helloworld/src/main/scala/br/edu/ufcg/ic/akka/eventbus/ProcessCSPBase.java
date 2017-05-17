package br.edu.ufcg.ic.akka.eventbus;

import akka.actor.UntypedActor;

public abstract class ProcessCSPBase extends UntypedActor {
	protected static enum State {
		started, deadlock, sucess, finished;
	}
	
	private State state;
	
	protected void init() {
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

	abstract protected void transition(State old, String event);	
	
	protected void syso(String msg){
		System.out.println(msg);
	}
}