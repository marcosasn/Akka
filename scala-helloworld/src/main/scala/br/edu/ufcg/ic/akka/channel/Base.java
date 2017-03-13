package br.edu.ufcg.ic.akka.channel;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public abstract class Base extends UntypedActor {
	/*
	 * This is the mutable state of this state machine.
	 */
	protected enum State {
		state_input, state_output, stop, skip;
	}

	private State state = State.state_input;
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	/*
	 * Then come all the mutator methods:
	 */
	protected void init() {}

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
	abstract protected void transition(State old, Object event, State next);
	
	protected void whenUnhandled(Object o) {
		log.warning("received unknown message {} in state {}", o, getState());
	}
	
	protected void log(String msg){
		log.info(msg);
	}
}