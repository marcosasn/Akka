package br.edu.ufcg.ic.akka.eventbus;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.Creator;
import akka.pattern.Patterns;
import akka.util.Timeout;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.Pair;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.GetInterState;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.InterState;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class ProcessCSP extends ProcessCSPBase {
	
	public interface ProcessCSPApi {
		public static class Pair {
			private ActorRef pair;
	        public Pair(ActorRef pair) {
	        	this.setPair(pair);
	        }
			public ActorRef getPair() {
				return pair;
			}
			public void setPair(ActorRef pair) {
				this.pair = pair;
			}
	    }
		
		public static class GetInterState {}
		
		public static class InterState {
			private State state;
	        public InterState() {
	        	this.state = null;
	        }
	        public InterState(State s) {
	        	setState(s);
	        }
			public State getState() {
				return state;
			}
			public void setState(State s) {
				this.state = s;
			}
		}
	}
	
	private static ScanningBusImpl scanningBus;
	private static ActorRef pair;
	
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
			} else if(message instanceof Pair){
				pair = ((Pair)message).getPair();
			} else if(message instanceof GetInterState){
				getSender().tell(new InterState(getState()), getSelf());	
			} else {
				if(pair != null){
					Timeout timeout = new Timeout(Duration.create(5, "seconds"));
					Future<Object> future = Patterns.ask(pair, new GetInterState(), timeout);
					InterState result = (InterState) Await.result(future, timeout.duration());
					if(result.getState() == State.stop){
						transition(getState(), "a");
						syso(message.toString() + "-" + getSelf().path().name() + "-" + getState());
					}
				}
			}
		} else if (getState() == State.stop){
			if(message instanceof GetInterState){
				getSender().tell(new InterState(getState()), getSelf());
			}
		}
	}

	@Override
	protected void transition(State old, String event) {
		if (old == State.started && event.equals("a")) {
			setState(State.stop);
		}
	}
}
