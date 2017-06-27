package br.edu.ufcg.ic.akka.eventbus;

import java.util.ArrayList;

import akka.actor.Props;
import akka.japi.Creator;

public class Stop extends ProcessCSPBase {
		
	public Stop() {
		super();
		super.initialize(new ArrayList<String>());
	}
	
	public static Props props() {
        return Props.create(new Creator<Stop>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Stop create() throws Exception {
                return new Stop();
            }

        });
    }

	@Override
	public void onReceive(Object message) throws Throwable {
		if(getState() == State.started){
			transition(getState(), ((String)message));
			syso(getSelf().path().name() + " got " + message.toString() + " state " + getState());
		}
	}

	@Override
	protected void transition(State old, String event) {
		if (old == State.started) {
			super.setState(State.deadlock);
			super.nextBehavior = super.deadlock;
			getContext().become(super.nextBehavior);
		}
	}
}
