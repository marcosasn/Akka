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
import akka.japi.Creator;
import br.edu.ufcg.ic.akka.java.MyUntypedActor;
import br.edu.ufcg.ic.akka.java.fsm.Buffer;

public class DeadLetterActor extends UntypedActor {

	public Props props() {
		return Props.create(new Creator<DeadLetterActor>() {
			private static final long serialVersionUID = 1L;

			@Override
			public DeadLetterActor create() throws Exception {
				return new DeadLetterActor();
			}

		});
	}

	public DeadLetterActor() {}

	public void onReceive(Object message) {
		if (message instanceof DeadLetter) {
			System.out.println(message);
		}
	}

	public static void main(String[] args) {
		Config config = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("DeadLetters", config.getConfig("akka.actor"));
		ActorRef actor = system.actorOf(Props.create(DeadLetterActor.class), "actor");
		System.out.println("Subscripting an actor in event stream from system... ");
		
		system.eventStream().subscribe(actor, DeadLetter.class);		
		system.eventStream().subscribe(actor, SuppressedDeadLetter.class);
		system.eventStream().subscribe(actor, AllDeadLetters.class);
		
		System.out.println("System has subscription: " + system.eventStream().hasSubscriptions(actor));
	}

}
