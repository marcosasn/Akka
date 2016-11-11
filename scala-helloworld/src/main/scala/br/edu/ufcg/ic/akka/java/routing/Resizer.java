package br.edu.ufcg.ic.akka.java.routing;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.DefaultResizer;
import akka.routing.RoundRobinPool;

public class Resizer extends UntypedActor{
	//Don't forget, you have set up the router29 into application.conf file into akka.actor.deployment{...}
	ActorRef router29 = getContext().actorOf(Props.create(Worker.class), "router29");
	
	DefaultResizer resizer = new DefaultResizer(2, 15);
	ActorRef router30 = getContext().actorOf(new RoundRobinPool(5).withResizer(resizer).props(
	Props.create(Worker.class)), "router30");

	@Override
	public void onReceive(Object msg) throws Throwable {
			if (msg instanceof Work) {
				//router29.tell(msg, getSender());
				router30.tell(msg, getSender());

			} else unhandled(msg); 
		}
	
	public static void main(String[] args) {
		Config conf = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", conf.getConfig("akka.actor"));
				
		ActorRef resizer = system.actorOf(Props.create(Resizer.class),"resizer");
		for(int i=0; i<5; i++){
			resizer.tell(new Work("ok"), ActorRef.noSender());
		}
	}
}
