package br.edu.ufcg.ic.akka.java;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class MainBufferProdutor {
	
	public static void main(String args[]){
		final ActorSystem system = ActorSystem.create("MySystem");
		final ActorRef buffer = system.actorOf(Props.create(Buffer.class),"buffer");
		final ActorRef produtor = system.actorOf(Props.create(Produtor.class, buffer),"produtor");
		
		produtor.tell(new Produtor.Produzir(), ActorRef.noSender());
	}

}
