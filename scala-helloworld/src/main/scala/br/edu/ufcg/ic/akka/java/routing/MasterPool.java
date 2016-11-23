package br.edu.ufcg.ic.akka.java.routing;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinPool;

public class MasterPool extends UntypedActor{
	ActorRef router1, router2;
	{
		//creating a router from configuration
		router1 = getContext().actorOf(Props.create(Worker.class),"router1");
		
		//Creating router without configuration
		router2 = getContext().actorOf(new RoundRobinPool(5).props(Props.create(Worker.class)),"router2");
	}

	public void onReceive(Object msg) {
		if (msg instanceof Work) {
			router1.tell(msg, getSender());
			//router2.tell(msg, getSender());
		} else if (msg instanceof Terminated) {
			//Nothing
		} else
			unhandled(msg);
	}
	
	public static void main(String[] args) {
		Config conf = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", conf.getConfig("akka.actor"));
		ActorRef masterPool = system.actorOf(Props.create(MasterPool.class),"masterPool");
		for(int i=0; i<5; i++){
			masterPool.tell(new Work("ok"), ActorRef.noSender());
		}
	}
}
