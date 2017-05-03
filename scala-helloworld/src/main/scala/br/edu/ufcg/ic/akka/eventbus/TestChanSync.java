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
	
		/*ActorRef p1 = system.actorOf(Props.create(ProcessCSP.class, scanningBus), "p1");
		ActorRef p2 = system.actorOf(Props.create(ProcessCSP.class, scanningBus), "p2");
		
		scanningBus.subscribe(p1, 3);
		scanningBus.subscribe(p2, 3);
		scanningBus.publish("a");*/
		
		/* Testando operador de prefixo*/
		ActorRef p3 = system.actorOf(Props.create(ProcessCSP.class, scanningBus), "p3");
		ActorRef p4 = system.actorOf(Props.create(ProcessCSP.class, scanningBus), "p4");
		scanningBus.subscribe(p3, 3);
		scanningBus.subscribe(p4, 3);
		
		ActorRef prefix = system.actorOf(Props.create(Prefix.class, scanningBus, p3, p4), "prefix"); 
		prefix.tell(new Prefix.PrefixApi.Perform(), ActorRef.noSender());
		prefix.tell(new Prefix.PrefixApi.Execute(), ActorRef.noSender());

	}
}
