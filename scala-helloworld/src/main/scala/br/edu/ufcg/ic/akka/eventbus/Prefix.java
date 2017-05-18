package br.edu.ufcg.ic.akka.eventbus;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import akka.pattern.Patterns;
import akka.util.Timeout;
import br.edu.ufcg.ic.akka.eventbus.Prefix.PrefixApi.Execute;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class Prefix extends UntypedActor {
	
	public interface PrefixApi {
		public static class Execute {    
	        public Execute() {}
	    }
	}

	private static ScanningBusImpl scanningBus;
	private List<String> events;
	private static ActorRef p1;
	private static ActorRef p2;

	public Prefix(ScanningBusImpl scanningBus, ActorRef p1, ActorRef p2) {
		Prefix.scanningBus = scanningBus;
		events = new ArrayList<String>();
		events.add("a");
		Prefix.p1 = p1;
		Prefix.p2 = p2;
	}
	
	public static Props props() {
        return Props.create(new Creator<Prefix>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Prefix create() throws Exception {
                return new Prefix(scanningBus, p1, p2);
            }

        });
    }

	public static ScanningBusImpl getScanningBus() {
		return scanningBus;
	}

	public List<String> getEvents() {
		return events;
	}

	public void setP1(ActorRef p1) {
		Prefix.p1 = p1;
	}

	public void setP2(ActorRef p2) {
		Prefix.p2 = p2;
	}
	
	public void peform() throws Throwable{
		/*Timeout timeout = new Timeout(Duration.create(5, "seconds"));
		Future<Object> future = Patterns.ask(p1, new GetInterState(), timeout);
		InterState result = (InterState) Await.result(future, timeout.duration());*/
		/*if(result.getState() == ProcessCSPBase.State.stop){
			p2.tell("a",getSelf());
		} else {
			timeout = new Timeout(Duration.create(5, "seconds"));
			future = Patterns.ask(p2, new GetInterState(), timeout);
			result = (InterState) Await.result(future, timeout.duration());
			if(result.getState() == ProcessCSPBase.State.stop){
				p1.tell("a",getSelf());
			}
		}*/
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		/*if(message instanceof Perform) {
			p1.tell(new ProcessCSP.ProcessCSPApi.Pair(p2), getSelf());
			p2.tell(new ProcessCSP.ProcessCSPApi.Pair(p1), getSelf());
			peform();
			
		} else if (message instanceof Execute){
			p1.tell("a", getSelf());
			peform();
		}*/
	}
}