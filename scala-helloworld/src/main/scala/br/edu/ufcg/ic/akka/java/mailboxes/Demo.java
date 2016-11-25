package br.edu.ufcg.ic.akka.java.mailboxes;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Demo extends UntypedActor{
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	public void onReceive(Object message) {
		log.info(message.toString());
	}
	
	public static void main(String[] args) {
		// We create a new Actor that just prints out what it processes
		Config conf = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", conf.getConfig("akka.actor"));
		//Don't forget! You have to set up the bridge actor name key to mailbox key 
		//ActorRef priomailboxactor = system.actorOf(Props.create(Demo.class).withMailbox("prio-mailbox"),"priomailboxactor");
		
		//Using dispatcher with mailbox
		System.out.println(system.dispatchers().hasDispatcher("prio-dispatcher"));
		ActorRef priomailboxactor = system.actorOf(Props.create(Demo.class).withDispatcher("prio-dispatcher"), "priomailboxactor");
		
		Object[] list = new Object[]{"lowpriority", "lowpriority","highpriority", "pigdog", "pigdog2", "pigdog3", "highpriority",
				PoisonPill.getInstance()};
		
		for (Object msg : list) {
			priomailboxactor.tell(msg, ActorRef.noSender());
		}
		
		System.out.println(system.mailboxes().lookup("prio-mailbox"));
		ActorRef priomailboxactor2 = system.actorOf(Props.create(Demo.class).withMailbox("prio-mailbox"), "priomailboxactor2");
		for (Object msg : list) {
			priomailboxactor2.tell(msg, ActorRef.noSender());
		}
		
		/*
			Logs:
			'highpriority
			'highpriority
			'pigdog
			'pigdog2
			'pigdog3
			'lowpriority
			'lowpriority
		*/
	}
}
