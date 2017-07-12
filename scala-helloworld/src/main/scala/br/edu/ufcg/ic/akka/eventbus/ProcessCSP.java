package br.edu.ufcg.ic.akka.eventbus;

import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.Perform;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSPBase.State;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.Execute;

import java.util.LinkedList;
import java.util.List;

import akka.actor.Props;
import akka.japi.Creator;
import akka.japi.Procedure;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.GetInitials;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.Initials;;

public class ProcessCSP extends ProcessCSPBase {

	public interface ProcessCSPApi {
		public static class Execute {
			public Execute() {
			}
		}

		public static class Perform {
			public String event;

			public Perform(String event) {
				this.event = event;
			}
		}

		public static class Initials {
			public LinkedList<String> events;

			public Initials(LinkedList<String> list) {
				this.events = list;
			}
		}

		public static class GetInterState {
			public GetInterState() {
			}
		}

		public static class InterState {
			public State state;

			public InterState(State state) {
				this.state = state;
			}

			public State getState() {
				return state;
			}
		}

		public static class GetInitials {
			public GetInitials() {
			}
		}
	}

	public ProcessCSP(List<String> initials) {
		super();
		super.initialize(initials);
	}

	public static Props props() {
		return Props.create(new Creator<ProcessCSP>() {
			private static final long serialVersionUID = 1L;

			@Override
			public ProcessCSP create() throws Exception {
				return new ProcessCSP(initials());
			}

		});
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if (getState() == State.started || getState() == State.executing) {
			if (message instanceof Perform) {
				super.peform(((Perform) message).event);

			} else if (message instanceof GetInitials) {
				getSender().tell(new Initials(initials()), getSelf());
			} else if (message instanceof Execute) {
				super.execute();
				
			} else if (message instanceof String && isCurrenteEvent((String) message)) {
				transition(getState(), ((String) message));
			}
		}
	}

	@Override
	protected void transition(State old, String event) {
		super.updateInitials();
		if (old == State.started && !initials().isEmpty()) {
			super.setState(State.executing);
			syso(getSelf().path().name() + " got " + event + " state " + getState());

		} else if (old == State.started && initials().isEmpty()){
			super.setState(State.deadlock);
			nextBehavior = super.deadlock;
			syso(getSelf().path().name() + " got " + event + " state " + getState());
			getContext().become(super.nextBehavior);
			
		} else if (old == State.executing && initials().isEmpty()) {
			super.setState(State.deadlock);
			nextBehavior = super.deadlock;
			syso(getSelf().path().name() + " got " + event + " state " + getState());
			getContext().become(super.nextBehavior);
		
		}
	}
}
