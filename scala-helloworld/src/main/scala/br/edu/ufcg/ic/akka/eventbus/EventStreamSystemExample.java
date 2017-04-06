package br.edu.ufcg.ic.akka.eventbus;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.AllDeadLetters;
import akka.actor.DeadLetter;
import akka.actor.Props;
import akka.actor.SuppressedDeadLetter;
import akka.actor.UntypedActor;
import br.edu.ufcg.ic.akka.java.MyUntypedActor;

class DeadLetterActor extends UntypedActor {
	public void onReceive(Object message) {
		if (message instanceof DeadLetter) {
			System.out.println(message);
		}
	}
}

public class EventStreamSystemExample {
	
	public static void main(String[] args) {
		Config config = ConfigFactory.load();
		final ActorSystem system = ActorSystem.create("DeadLetters", config.getConfig("akka.actor"));
		final ActorRef actor = system.actorOf(Props.create(DeadLetterActor.class));
		system.eventStream().subscribe(actor, DeadLetter.class);
		//system.eventStream().subscribe(actor, SuppressedDeadLetter.class);
		//system.eventStream().subscribe(actor, AllDeadLetters.class);
	}

}
