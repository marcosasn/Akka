package br.edu.ufcg.ic.akka.eventbus;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.Perform;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.GetInterState;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.InterState;;

public class SynchronousParallelComposition extends ProcessCSPBase {
	
	public interface PrefixApi {
		public static class Execute {    
	        public Execute() {}
	    }
	}

	private static ScanningBusImpl scanningBus;
	private ActorRef p1;
	private ActorRef p2;

	public SynchronousParallelComposition(ScanningBusImpl scanningBus) {
		SynchronousParallelComposition.scanningBus = scanningBus;
		
		p1 = getContext().actorOf(Props.create(ProcessCSP.class), "p1");
		p2 = getContext().actorOf(Props.create(ProcessCSP.class), "p2");
	}
	
	public static Props props() {
        return Props.create(new Creator<SynchronousParallelComposition>() {
            private static final long serialVersionUID = 1L;

            @Override
            public SynchronousParallelComposition create() throws Exception {
                return new SynchronousParallelComposition(scanningBus);
            }

        });
    }

	public static ScanningBusImpl getScanningBus() {
		return scanningBus;
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if(message instanceof String && ((String)message).equals("a")){
			p1.tell(new Perform((String)message), getSelf());
			Timeout timeout = new Timeout(Duration.create(5, "seconds"));
			Future<Object> future = Patterns.ask(p1, new GetInterState(), timeout);
			InterState result = (InterState) Await.result(future, timeout.duration());
			if(result.getState() == ProcessCSPBase.State.deadlock){
				p2.tell(new Perform((String)message), getSelf());
			
				timeout = new Timeout(Duration.create(5, "seconds"));
				future = Patterns.ask(p2, new GetInterState(), timeout);
				result = (InterState) Await.result(future, timeout.duration());
				
			}
		}
	}

	@Override
	protected void transition(State old, String event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void peform(String event) {
		getSelf().tell(event, getSelf());		
	}
}