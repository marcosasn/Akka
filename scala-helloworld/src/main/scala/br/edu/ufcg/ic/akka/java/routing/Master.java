package br.edu.ufcg.ic.akka.java.routing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

final class Work implements Serializable {
	private static final long serialVersionUID = 1L;
	public final String payload;

	public Work(String payload) {
		this.payload = payload;
	}
}

final class Worker extends UntypedActor {

	public void onReceive(Object msg) {
		if (msg instanceof Work) {
			//work
		} else {
			unhandled(msg);
		}
	}
  }

public class Master extends UntypedActor {
	//immutable
	Router router;
	{
		List<Routee> routees = new ArrayList<Routee>();
		for (int i = 0; i < 5; i++) {
			ActorRef r = getContext().actorOf(Props.create(Worker.class));
			getContext().watch(r);
			routees.add(new ActorRefRoutee(r));
		}
		// RoundRobinRoutinlogic is thread safe, can be used outside actor
		router = new Router(new RoundRobinRoutingLogic(), routees);
	}

	public void onReceive(Object msg) {
		if (msg instanceof Work) {
			router.route(msg, getSender());
		} else if (msg instanceof Terminated) {
			router = router.removeRoutee(((Terminated) msg).actor());
			ActorRef r = getContext().actorOf(Props.create(Worker.class));
			getContext().watch(r);
			router = router.addRoutee(new ActorRefRoutee(r));
		} else
			unhandled(msg);
	}
}