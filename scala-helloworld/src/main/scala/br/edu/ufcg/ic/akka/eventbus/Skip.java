package br.edu.ufcg.ic.akka.eventbus;

import java.util.ArrayList;
import java.util.List;

import akka.actor.Props;
import akka.japi.Creator;

public class Skip extends ProcessCSPBase {
	
	public Skip() {
		super();
		super.initialize();
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
			if (message instanceof String){
				transition(getState(), ((String)message));
			}
		}
	}

	@Override
	protected void transition(State old, String event) {
		if (old == State.started && event.equals("tick")) {
			super.setState(State.deadlock);
			syso(getSelf().path().name() + " got " + event + " state " + getState());
			getContext().become(super.deadlock);
		}
	}
	
	@Override
	protected List<String> initials() {
		List<String> inits = new ArrayList<String>();
		inits.add("tick");
		return inits;
	}
}
