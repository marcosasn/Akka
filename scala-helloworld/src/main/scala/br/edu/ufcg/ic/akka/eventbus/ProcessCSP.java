package br.edu.ufcg.ic.akka.eventbus;

import akka.actor.Props;
import akka.japi.Creator;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.Perform;

public class ProcessCSP extends ProcessCSPBase {
	
	public interface ProcessCSPApi {
		public static class Perform {
			public String event;
			
	        public Perform(String event) {
	        	this.event = event;
	        }
	    }
	}
	
	private static ScanningBusImpl scanningBus;
	
	public ProcessCSP(ScanningBusImpl scanningBus) {
		super();
		super.initialize();
		ProcessCSP.scanningBus = scanningBus;
	}
	
	public static Props props() {
        return Props.create(new Creator<ProcessCSP>() {
            private static final long serialVersionUID = 1L;

            @Override
            public ProcessCSP create() throws Exception {
                return new ProcessCSP(scanningBus);
            }

        });
    }

	@Override
	public void onReceive(Object message) throws Throwable {
		if(getState() == State.started){
			if(message instanceof Perform) {
				peform(((Perform)message).event);
				
			}
			else if(message instanceof String && ((String)message).equals("a")){
				transition(getState(), ((String)message));
				syso(message.toString() + "-" + getSelf().path().name() + "-" + getState());
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
