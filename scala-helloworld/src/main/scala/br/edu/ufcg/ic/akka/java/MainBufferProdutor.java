package br.edu.ufcg.ic.akka.java;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class MainBufferProdutor {
	
	public static void main(String args[]){
		final ActorSystem system = ActorSystem.create("MySystem");
		
		final ActorRef buffer = system.actorOf(Props.create(Buffer.class, 10),"buffer");
		final ActorRef produtor = system.actorOf(Props.create(Produtor.class, buffer),"produtor");
		final ActorRef consumidor = system.actorOf(Props.create(Consumidor.class, buffer),"consumidor");
		
		/*Informando o tempo de produção consumo em milisegundos(10E-3)*/
		produtor.tell(new Consumidor.TempoEspera(1000), ActorRef.noSender());
		consumidor.tell(new Consumidor.TempoEspera(1000), ActorRef.noSender());
		
		produtor.tell(new Produtor.Produzir(), ActorRef.noSender());
		consumidor.tell(new Consumidor.Consumir(), ActorRef.noSender());
	}
}
