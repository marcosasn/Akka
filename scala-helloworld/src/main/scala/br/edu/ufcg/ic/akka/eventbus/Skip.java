package br.edu.ufcg.ic.akka.eventbus;

import java.util.ArrayList;
import java.util.List;

import akka.actor.Props;
import akka.japi.Creator;

public class Skip extends ProcessCSPBase {
		
	public Skip() {
		super();
		
		List<String> in = new ArrayList<String>();
		in.add("tick");
		super.initialize(in);
	}
	
	public static Props props() {
        return Props.create(new Creator<Skip>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Skip create() throws Exception {
                return new Skip();
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
		if (old == State.started && event.equals("tick")) {
			super.setState(State.deadlock);
			super.nextBehavior = super.deadlock;
			getContext().become(super.nextBehavior);
		}
	}
}
