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
import akka.routing.RandomGroup;
import akka.routing.RandomPool;
import akka.routing.RoundRobinGroup;
import akka.routing.RoundRobinPool;

public class Random extends UntypedActor {
	//RandomPool deﬁned in conﬁguration:
	ActorRef router5 = getContext().actorOf(Props.create(Worker.class), "router5");
	
	//RandomPool deﬁned in code
	ActorRef router6 = getContext().actorOf(new RandomPool(5).props(Props.create(Worker.class)),
			"router6");
	
	//RandomGroup deﬁned in conﬁguration:
	ActorRef router7 = getContext().actorOf(Props.create(Worker.class), "router7");
	
	//RandomGroup deﬁned in code
	List<String> paths = Arrays.asList("/user/workers/w1", "/user/workers/w2","/user/workers/w3");
	ActorRef router8 = getContext().actorOf(new RandomGroup(paths).props(), "router8");
	
	@Override
	public void onReceive(Object msg) throws Throwable {
		if (msg instanceof Work) {
			//router6.tell(msg, getSender());
			//router5.tell(msg, getSender());
			//router7.tell(msg, getSender());
			router8.tell(msg, getSender());

		} else unhandled(msg);
	}
	
	public static void main(String[] args) {
		Config conf = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", conf.getConfig("akka.actor"));
		// workers created externally
		system.actorOf(Props.create(Workers.class), "workers");
				
		ActorRef random = system.actorOf(Props.create(Random.class),"random");
		
		for(int i=0; i<5; i++){
			random.tell(new Work("ok"), ActorRef.noSender());
		}
	}
}