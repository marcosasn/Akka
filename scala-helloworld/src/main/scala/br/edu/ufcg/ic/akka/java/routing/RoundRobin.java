package br.edu.ufcg.ic.akka.java.routing;

import java.util.Arrays;
import java.util.List;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.FromConfig;
import akka.routing.RoundRobinGroup;
import akka.routing.RoundRobinPool;

public class RoundRobin extends UntypedActor {
	//RoundRobinPool deﬁned in conﬁguration:
	ActorRef router1 = getContext().actorOf(Props.create(Worker.class),"router1");
	//RoundRobinPool deﬁned in code
	ActorRef router2 = getContext().actorOf(new RoundRobinPool(5).props(Props.create(Worker.class)),
			"router2");
	
	//RoundRobinGroup deﬁned in conﬁguration:
	ActorRef router3 = getContext().actorOf(Props.create(Worker.class), "router3");
	
	//RoundRobinGroup deﬁned in code:
	List<String> paths = Arrays.asList("/user/workers/w1", "/user/workers/w2", "/user/workers/w3");
	ActorRef router4 = getContext().actorOf(new RoundRobinGroup(paths).props(), "router4");

	@Override
	public void onReceive(Object msg) throws Throwable {
		if (msg instanceof Work) {
			//router1.tell(msg, getSender());
			//router2.tell(msg, getSender());
			//router4.tell(msg, getSender());
			router3.tell(msg, getSender());

		} else unhandled(msg);
	}
	
	public static void main(String[] args) {
		Config conf = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", conf.getConfig("akka.actor"));
		// workers created externally
		system.actorOf(Props.create(Workers.class), "workers");
				
		ActorRef roundRobin = system.actorOf(Props.create(RoundRobin.class),"roundRobin");
		
		for(int i=0; i<5; i++){
			roundRobin.tell(new Work("ok"), ActorRef.noSender());
		}
	}
}
