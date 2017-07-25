package br.edu.ufcg.ic.akka.eventbus;

import java.util.ArrayList;
import java.util.List;

import akka.actor.Props;
import akka.japi.Creator;

public class Skip extends ProcessCSPBase {
	
	private List<Event> inits;
	
	public Skip() {
		super();
		super.initialize();
		inits = new ArrayList<Event>();
		inits.add(new Tick());
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
			if (message instanceof Event){
				transition(getState(), (Event)message);
			}
		}
	}

	@Override
	protected void transition(State old, Event event) {
		if (old == State.started)
			if (event instanceof Tick){
				if (((Tick)event).equals(new Tick())) {
					super.setState(State.deadlock);
					syso(getSelf().path().name() + " got " + ((Tick)event).toString() + " state " + getState());
					getContext().become(super.deadlock);
				}
			}
	}
	
	@Override
	protected List<Event> initials() {
		return inits;
	}
}
