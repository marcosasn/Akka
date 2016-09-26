package br.edu.ufcg.ic.akka.java;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class MainBufferProdutor {
	
	public static void main(String args[]){
		final ActorSystem system = ActorSystem.create("MySystem");
		
		final ActorRef buffer = system.actorOf(Props.create(Buffer.class, 10),"buffer");
		final ActorRef produtor = system.actorOf(Props.create(Produtor.class, buffer, 5),"produtor");
		final ActorRef consumidor = system.actorOf(Props.create(Consumidor.class, buffer, 2),"consumidor");
		
		/*final ActorRef produtor1 = system.actorOf(Props.create(Produtor.class, buffer, 5),"produtor1");
		final ActorRef consumidor1 = system.actorOf(Props.create(Consumidor.class, buffer, 8),"consumidor1");*/
		
		//First case
		produtor.tell(new Produtor.Produzir(), ActorRef.noSender());
		consumidor.tell(new Consumidor.Consumir(), ActorRef.noSender());
		/*produtor1.tell(new Produtor.Produzir(), ActorRef.noSender());
		consumidor1.tell(new Consumidor.Consumir(), ActorRef.noSender());*/
		
		/*consumidor.tell(new Consumidor.Consumir(), ActorRef.noSender());
		produtor.tell(new Produtor.Produzir(), ActorRef.noSender());
		consumidor.tell(new Consumidor.Consumir(), ActorRef.noSender());*/
		
	}
}
