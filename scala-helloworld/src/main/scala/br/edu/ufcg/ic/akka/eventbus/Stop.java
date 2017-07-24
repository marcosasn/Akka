package br.edu.ufcg.ic.akka.eventbus;

import java.util.ArrayList;
import java.util.List;

import akka.actor.Props;
import akka.japi.Creator;

public class Stop extends ProcessCSPBase {
		
	public Stop() {
		super();
		super.initialize();
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
		}
	}

	@Override
	protected void transition(State old, Object event) {
		if (old == State.started) {
			super.setState(State.deadlock);
			syso(getSelf().path().name() + " got " + event + " state " + getState());
			getContext().become(super.deadlock);
		}
	}

	@Override
	protected List<Event> initials() {
		return new ArrayList<Event>();
	}
}
