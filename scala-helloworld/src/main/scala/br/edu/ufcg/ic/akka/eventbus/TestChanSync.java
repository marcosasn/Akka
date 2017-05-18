package br.edu.ufcg.ic.akka.eventbus;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class TestChanSync {

	public static void main(String[] args) throws InterruptedException {
		Config config = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", config.getConfig("akka.actor"));
		/* Testando processos simples*/
		ScanningBusImpl scanningBus = new ScanningBusImpl();
	
		/*ActorRef p1 = system.actorOf(Props.create(ProcessCSP.class), "p1");
		ActorRef p2 = system.actorOf(Props.create(ProcessCSP.class), "p2");
		
		scanningBus.subscribe(p1, 3);
		scanningBus.subscribe(p2, 3);
		scanningBus.publish("a");
		scanningBus.publish("a");
		scanningBus.publish("a");*/
		
		/* Testando operador de prefixo*/
		ActorRef spc = system.actorOf(Props.create(SynchronousParallelComposition.class, scanningBus), "spc"); 
		scanningBus.subscribe(spc, 3);
		scanningBus.publish("a");
	}
}
