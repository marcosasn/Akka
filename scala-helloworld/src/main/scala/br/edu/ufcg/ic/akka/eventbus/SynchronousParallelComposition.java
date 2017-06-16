package br.edu.ufcg.ic.akka.eventbus;

import java.util.ArrayList;
import java.util.List;

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
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.Initials;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.GetInitials;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.InterState;;

public class SynchronousParallelComposition extends UntypedActor {

	public interface PrefixApi {
		public static class Execute {
			public Execute() {
			}
		}
	}

	private static ScanningBusImpl scanningBus;
	private ActorRef p3;
	private ActorRef p4;

	public SynchronousParallelComposition(ScanningBusImpl scanningBus) {
		SynchronousParallelComposition.scanningBus = scanningBus;
		List<String> initials = new ArrayList<String>();
		initials.add("a");
		p3 = getContext().actorOf(Props.create(ProcessCSP.class, initials), "p3");
		p4 = getContext().actorOf(Props.create(ProcessCSP.class, initials), "p4");
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

	@Override
	public void onReceive(Object message) throws Throwable {
		if (message instanceof String) {
			Timeout timeout = new Timeout(Duration.create(5, "seconds"));
			Future<Object> future = Patterns.ask(p3, new GetInitials(), timeout);
			Initials r1 = (Initials) Await.result(future, timeout.duration());
			
			future = Patterns.ask(p4, new GetInitials(), timeout);
			Initials r2 = (Initials) Await.result(future, timeout.duration());
			
			if (r1.events.getFirst().equals(r2.events.getFirst()) &&
					r1.events.getFirst().equals((String)message)) {
				
				p3.tell(new Perform((String)message), getSelf());
				timeout = new Timeout(Duration.create(5, "seconds"));
				future = Patterns.ask(p3, new GetInterState(), timeout);
				InterState result = (InterState) Await.result(future, timeout.duration());
				if (result.getState() == ProcessCSPBase.State.deadlock) {
					p4.tell(new Perform((String) message), getSelf());
					timeout = new Timeout(Duration.create(5, "seconds"));
					future = Patterns.ask(p4, new GetInterState(), timeout);
					result = (InterState) Await.result(future, timeout.duration());
				}
			}
		}
	}
}