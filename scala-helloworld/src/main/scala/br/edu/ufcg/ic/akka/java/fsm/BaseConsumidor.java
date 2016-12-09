package br.edu.ufcg.ic.akka.java.fsm;

import akka.actor.UntypedActor;

public abstract class BaseConsumidor extends UntypedActor {
	/*
	 * This is the mutable state of this state machine.
	 */
	protected enum State {
		OUTPUT;
	}

	private State state = State.OUTPUT;

	/*
	 * Then come all the mutator methods:
	 */
	protected void init() {	}

	protected void setState(State s) {
		if (state != s) {
			//transition(state, s);
			state = s;
		}
	}

	/**
		Here are
		the interrogation methods:
	*/
	protected State getState() {
		return state;
	}

	/**	And finally
		the callbacks (only one in this example: react to state change)
	*/
	abstract protected void transition(State old, String event, State next);
}