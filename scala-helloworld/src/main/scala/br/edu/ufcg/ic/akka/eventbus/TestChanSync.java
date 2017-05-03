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
		
		ScanningBusImpl scanningBus = new ScanningBusImpl();
	
		ActorRef p1 = system.actorOf(Props.create(ProcessCSP.class, scanningBus), "p1");
		ActorRef p2 = system.actorOf(Props.create(ProcessCSP.class, scanningBus), "p2");
		
		scanningBus.subscribe(p1, 3);
		scanningBus.subscribe(p2, 3);
		scanningBus.publish("a");
		
		/*
        //cria channels, readers and writers
        ActorRef reader = system.actorOf(Props.create(Reader.class, system), "reader");
        ActorRef reader2 = system.actorOf(Props.create(Reader.class, system), "reader2");
        ActorRef reader3 = system.actorOf(Props.create(Reader.class, system), "reader3");
        ActorRef writer = system.actorOf(Props.create(Writer.class, system), "writer");
        
        system.eventStream().subscribe(reader, Input.class);
        system.eventStream().subscribe(reader2, Input.class);
        system.eventStream().subscribe(reader3, Input.class);
        system.eventStream().subscribe(writer, Command.class);
        
        reader.tell(new Reader.Start(), ActorRef.noSender());
        reader2.tell(new Reader.Start(), ActorRef.noSender());
        reader3.tell(new Reader.Start(), ActorRef.noSender());  
  		
        //espera um pouco para todos os atores iniciarem
        Thread.sleep(1000);
        
        System.out.println("iniciando o writer");
        //inicia o writer e ele comunica o output no canal que vai destravar os leitores
        writer.tell(new Reader.Start(), ActorRef.noSender());
        
        Thread.sleep(1000);
        system.terminate();
        */
	}
}
