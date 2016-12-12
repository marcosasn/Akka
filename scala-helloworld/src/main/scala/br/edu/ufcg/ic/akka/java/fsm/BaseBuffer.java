package br.edu.ufcg.ic.akka.java.fsm;

import java.util.ArrayList;
import java.util.List;

import akka.actor.UntypedActor;

public abstract class BaseBuffer extends UntypedActor {
	/*
	 * This is the mutable state of this state machine.
	 */
	protected enum State {
		SIZE_0, SIZE_1, SIZE_2;
	}

	private State state = State.SIZE_0;
	private List<Integer> numbers = new ArrayList<Integer>();

	/*
	 * Then come all the mutator methods:
	 */
	protected void init() {
	}

	protected void setState(State s) {
		if (state != s) {
			// transition(state, s);
			state = s;
		}
	}
	
	protected void addNumber(Integer i) {
		numbers.add(i);
	}

	/**
	 * Here are the interrogation methods:
	 */
	protected State getState() {
		return state;
	}

	protected List<Integer> getNumbers() {
		return numbers;
	}

	/**
	 * And finally the callbacks (only one in this example: react to state
	 * change)
	 */
	abstract protected void transition(State old, String event, State next);
}