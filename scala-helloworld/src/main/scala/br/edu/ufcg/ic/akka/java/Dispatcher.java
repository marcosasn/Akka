package br.edu.ufcg.ic.akka.java;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import scala.concurrent.ExecutionContext;

public class Dispatcher {
	public static void main(String[] args) {
		
		Config conf = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", conf.getConfig("akka.actor"));
		ActorRef myactor = system.actorOf(Props.create(MyUntypedActor.class), "myactor");
		System.out.println(system.settings());
		
		// alternative way
		ActorRef myactor3 = system.actorOf(Props.create(MyUntypedActor.class).withDispatcher("my-dispatcher"),
				"myactor3");
		
		// this is scala.concurrent.ExecutionContext
		// for use with Futures, Scheduler, etc.
		final ExecutionContext ex = system.dispatchers().lookup("my-dispatcher");
		final ExecutionContext ex2 = system.dispatchers().lookup("my-thread-pool-dispatcher");
				
		try{
			assert ex != null && ex2 != null;
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	//TODO p.133
}