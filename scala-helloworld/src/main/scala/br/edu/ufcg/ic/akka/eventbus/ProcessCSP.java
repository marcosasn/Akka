package br.edu.ufcg.ic.akka.eventbus;

import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.Perform;

import java.util.List;

import akka.actor.Props;
import akka.japi.Creator;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.GetInitials;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.Initials;;

public class ProcessCSP extends ProcessCSPBase {
	
	public interface ProcessCSPApi {
		public static class Perform {
			public String event;
			
	        public Perform(String event) {
	        	this.event = event;
	        }
	    }
		
		public static class Initials {
			public List<String> events;
			
	        public Initials(List<String> list) {
	        	this.events = list;
	        }
	    }
		
		public static class GetInterState {
	        public GetInterState() {  }
		}
		
		public static class InterState {
			public State state;
			
	        public InterState(State state) {
	        	this.state = state;
	        }
	        
	        public State getState(){
	        	return state;
	        }
		}
		
		public static class GetInitials {
	        public GetInitials() {   }
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
                return new ProcessCSP(initials);
            }

        });
    }

	@Override
	public void onReceive(Object message) throws Throwable {
		if(getState() == State.started){
			if(message instanceof Perform) {
				peform(((Perform)message).event);
				
			}
			else if(message instanceof GetInitials){
				getSender().tell(new Initials(getInitials()), getSelf());
			}
			else if(message instanceof String && getInitials().getFirst().equals((String)message)){
				transition(getState(), ((String)message));
				syso(message.toString() + "-" + getSelf().path().name() + "-" + getState());
				updateInitials();
			} 
		}
	}

	@Override
	protected void transition(State old, String event) {
		if (old == State.started && event.equals("a")) {
			setState(State.deadlock);
			getContext().become(super.deadlock);
		}
	}

	@Override
	protected void peform(String event) {
		getSelf().tell(event, getSelf());
	}
}
