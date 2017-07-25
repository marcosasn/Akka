package br.edu.ufcg.ic.akka.eventbus;

import java.util.ArrayList;
import java.util.List;

import akka.actor.Props;
import akka.japi.Creator;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSPBase.State;

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
		if (getState() == State.started) {
			if (message instanceof Event) {
				transition(getState(), (Event)message);
			}
		}
	}

	@Override
	protected void transition(State old, Event event) {
		if (old == State.started) {
			if (event instanceof TypedEvent) {
				super.setState(State.deadlock);
				syso(getSelf().path().name() + " got " + ((TypedEvent)event).getMessage() + " state " + getState());
				getContext().become(super.deadlock);

			} else if (event instanceof UntypedEvent) {

			}

		}
	}

	@Override
	protected List<Event> initials() {
		return new ArrayList<Event>();
	}
}
