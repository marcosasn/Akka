package br.edu.ufcg.ic.akka.java.fsm;

import java.util.LinkedList;
import java.util.List;

import akka.actor.UntypedActor;

public abstract class BaseBuffer extends UntypedActor {
	/*
	 * This is the mutable state of this state machine.
	 */
	protected enum State {
		SIZE_0, SIZE_1, SIZE_2;
	}

	private State state;
	private LinkedList<Integer> numbers;

	/*
	 * Then come all the mutator methods:
	 */
	protected void init() {
		state = State.SIZE_0;
		numbers = new LinkedList<Integer>();
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
	
	protected Integer removeFirst(){
		return numbers.removeFirst();
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
	
	protected boolean isFull(){
		return numbers.size() == 2;
	}
	
	protected boolean isEmpty(){
		return numbers.isEmpty();
	}

	/**
	 * And finally the callbacks (only one in this example: react to state
	 * change)
	 */
	abstract protected void transition(State old, Object event);
}