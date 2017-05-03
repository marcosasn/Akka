package br.edu.ufcg.ic.akka.eventbus;

import akka.actor.Props;
import akka.japi.Creator;

public class ProcessCSP extends ProcessCSPBase {
	
	private static ScanningBusImpl scanningBus;
	
	public ProcessCSP(ScanningBusImpl scanningBus) {
		super();
		super.init();
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
			if(message instanceof String && ((String)message).equals("a")){
				transition(getState(), ((String)message));
				syso(message.toString() + "-" + getSelf().path().name() + "-" + getState());
			} else {
				syso(message.toString());
			}
		}
	}

	@Override
	protected void transition(State old, Object event) {
		if (old == State.started && event.equals("a")) {
			setState(State.stop);
		}
	}
}
