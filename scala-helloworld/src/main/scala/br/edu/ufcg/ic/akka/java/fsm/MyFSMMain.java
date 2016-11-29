package br.edu.ufcg.ic.akka.java.fsm;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import br.edu.ufcg.ic.akka.java.MyUntypedActor;
import br.edu.ufcg.ic.akka.java.fsm.MyFSM.MyFSMApi.SetTarget;
import br.edu.ufcg.ic.akka.java.fsm.MyFSM.MyFSMApi.Queue;

public class MyFSMMain {
	public static void main(String[] args) {
		Config config = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", config.getConfig("akka.actor"));
		ActorRef myFSM = system.actorOf(Props.create(MyFSM.class), "myFSM");
		ActorRef target = system.actorOf(Props.create(MyUntypedActor.class), "target");
		
		myFSM.tell("new SetTarget(target)", ActorRef.noSender());
		myFSM.tell(new SetTarget(target), ActorRef.noSender());
		myFSM.tell(new Queue(new Object()), ActorRef.noSender());
		myFSM.tell(MyFSM.MyFSMApi.flush, ActorRef.noSender());	
	}
}
