package br.edu.ufcg.ic.akka.java.routing;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RandomPool;

public class ConfiguringDispatchers {
	public static void main(String[] args) {
		Config conf = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", conf.getConfig("akka.actor"));
		Props props =
				// “head” router actor will run on "router-dispatcher" dispatcher
				// Worker routees will run on "pool-dispatcher" dispatcher
				new RandomPool(5).withDispatcher("router-dispatcher").props(Props.create(Worker.class));
				//new RandomPool(5).props(Props.create(Worker.class));
		ActorRef router = system.actorOf(props, "poolWithDispatcher");
		for(int i=0; i<5; i++){
			router.tell(new Work("ok"), ActorRef.noSender());
		}
	}

}
