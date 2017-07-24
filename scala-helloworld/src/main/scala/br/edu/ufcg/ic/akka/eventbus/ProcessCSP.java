package br.edu.ufcg.ic.akka.eventbus;

import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.SetBehavior;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.AddInitial;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.Execute;

import java.util.ArrayList;
import java.util.List;

import akka.actor.Props;
import akka.japi.Creator;
import akka.japi.Procedure;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.GetInitials;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.Initials;;

public class ProcessCSP extends ProcessCSPBase {
	
	Procedure<Object> nextBehavior;
	private List<Event> inits;

	public interface ProcessCSPApi {
		public static class Execute {
			public Execute() {
			}
		}

		public static class AddInitial {
			public Event event;

			public AddInitial(Event event) {
				this.event = event;
			}
		}

		public static class Initials {
			public List<Event> events;

			public Initials(List<Event> list) {
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
		
		public static class SetBehavior {
			public Procedure<Object> prc;

			public SetBehavior(Procedure<Object> prc) {
				this.prc = prc;
			}

			public Procedure<Object> getBehavior() {
				return prc;
			}
		}

		public static class GetInitials {
			public GetInitials() {
			}
		}
	}

	public ProcessCSP() {
		super();
		super.initialize();
		inits = new ArrayList<Event>();
	}

	public static Props props() {
		return Props.create(new Creator<ProcessCSP>() {
			private static final long serialVersionUID = 1L;

			@Override
			public ProcessCSP create() throws Exception {
				return new ProcessCSP();
			}

		});
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if (getState() == State.started) {
			if (message instanceof AddInitial) {
				inits.add(((AddInitial)message).event);

			} else if (message instanceof GetInitials) {
				getSender().tell(new Initials(inits), getSelf());
			} else if (message instanceof Execute) {
				execute();
				
			} else if (message instanceof SetBehavior){
				nextBehavior = ((SetBehavior)message).getBehavior();
				
			} else if (message instanceof String && isCurrenteEvent((String) message)) {
				if(nextBehavior != null){
					syso(getSelf().path().name() + " got " + ((String) message) + " state deadlock");
					getContext().become(nextBehavior);
				}
			}
		}
	}

	@Override
	protected void transition(State old, Object event) {
		/*if (old == State.started && !initials().isEmpty()) {
			super.setState(State.executing);

		} else if (old == State.started && initials().isEmpty()){
			super.setState(State.deadlock);
			
		} else if (old == State.executing && initials().isEmpty()) {
			super.setState(State.deadlock);
			
		}*/
	}
	
	@Override
	protected List<Event> initials() {
		return inits;
	}
	
	private boolean isCurrenteEvent(String message) {
		if(!inits.isEmpty()){
			return inits.get(0).equals(message);
		}
		return false;
	}
	
	private void execute() {
		if(!inits.isEmpty()){
			super.peform(inits.get(0));
		}
	}
}
