package br.edu.ufcg.ic.akka.eventbus;

import java.util.ArrayList;
import java.util.List;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class TestChanSync {

	public static void main(String[] args) throws InterruptedException {
		Config config = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", config.getConfig("akka.actor"));
		
		/* STOP*/
		ActorRef stop = system.actorOf(Props.create(Stop.class), "stop");
		stop.tell("hello", ActorRef.noSender());
		stop.tell("hello2", ActorRef.noSender());

		
		/* SKIP*/
		ActorRef skip = system.actorOf(Props.create(Skip.class), "skip");
		skip.tell("hello", ActorRef.noSender());
		skip.tell("tick", ActorRef.noSender());
		skip.tell("hello2", ActorRef.noSender());
		
		/* Testando processos simples a->STOP*/
		ScanningBusImpl scanningBus = new ScanningBusImpl();
		/*List<String> initials = new ArrayList<String>();
		initials.add("a");
		ActorRef p1 = system.actorOf(Props.create(ProcessCSP.class, initials), "p1");
		ActorRef p2 = system.actorOf(Props.create(ProcessCSP.class, initials), "p2");
		
		scanningBus.subscribe(p1, 3);
		scanningBus.subscribe(p2, 3);
		scanningBus.publish("a");
		scanningBus.publish("a");
		scanningBus.publish("a");*/
		
		/* Testando operador de prefixo
		 * a->STOP || a->STOP*/
		/*ActorRef spc = system.actorOf(Props.create(SynchronousParallelComposition.class, scanningBus), "spc"); 
		scanningBus.subscribe(spc, 3);
		scanningBus.publish("a");
		*/
		/*a -> b -> SKIP || c -> STOP = isso deve ser igual a STOP*/
		/*String[] initialsP1 = new String[]{"a","b"};
		String[] initialsP2 = new String[]{"c"};
		ActorRef spc2 = system.actorOf(Props.create(SynchronousParallelComposition.class, scanningBus), "spc2");
		scanningBus.subscribe(spc, 3);*/
	}
}
