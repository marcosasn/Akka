package br.edu.ufcg.ic.akka.java.routing;

import java.util.Arrays;
import java.util.List;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinGroup;
import akka.routing.RoundRobinPool;

final class Workers extends UntypedActor {
	@Override
	public void preStart() {
		getContext().actorOf(Props.create(Worker.class), "w1");
		getContext().actorOf(Props.create(Worker.class), "w2");
		getContext().actorOf(Props.create(Worker.class), "w3");
	}

	@Override
	public void onReceive(Object arg0) throws Throwable {
		// TODO Auto-generated method stub
	}
}

public class MasterGroup extends UntypedActor{
	//by application.conf
	ActorRef router3 = getContext().actorOf(Props.create(Worker.class), "router3");
	//without application.conf configuration
	List<String> paths = Arrays.asList("/user/workers/w1", "/user/workers/w2", "/user/workers/w3");
	ActorRef router4 = getContext().actorOf(new RoundRobinGroup(paths).props(), "router4");
	
	public void onReceive(Object msg) {
		if (msg instanceof Work) {
			//router3.tell(msg, getSender());
			router4.tell(msg, getSender());
		} else if (msg instanceof Terminated) {
			//Nothing
		} else
			unhandled(msg);
	}
	
	public static void main(String[] args) {
		Config conf = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", conf.getConfig("akka.actor"));
		// workers created externally
		system.actorOf(Props.create(Workers.class), "workers");
				
		ActorRef masterGroup = system.actorOf(Props.create(MasterGroup.class),"masterGroup");
		
		for(int i=0; i<5; i++){
			masterGroup.tell(new Work("ok"), ActorRef.noSender());
		}
	}
}
