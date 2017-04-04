package br.edu.ufcg.ic.akka.eventbus;

import java.util.concurrent.TimeUnit;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.japi.ManagedActorEventBus;
import akka.testkit.JavaTestKit;
import br.edu.ufcg.ic.akka.eventbus.ActorBusImpl.Notification;
import scala.concurrent.duration.FiniteDuration;

public class ActorBusImpl extends ManagedActorEventBus<Notification> {
	
	public static class Notification {
		public final ActorRef ref;
		public final int id;

		public Notification(ActorRef ref, int id) {
			this.ref = ref;
			this.id = id;
		}
		
		public String toString() {
			return "Notification " + this.id + " ref: " + this.ref;
		}
	}
	
	// the ActorSystem will be used for book-keeping operations, such as
	// subscribers terminating
	public ActorBusImpl(ActorSystem system) {
		super(system);
	}

	// is used for extracting the classifier from the incoming events
	@Override
	public ActorRef classify(Notification event) {
		return event.ref;
	}

	// determines the initial size of the index data structure
	// used internally (i.e. the expected number of different classifiers)
	@Override
	public int mapSize() {
		return 128;
	}
	
	public static void main(String[] args){
		Config config = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", config.getConfig("akka.actor"));
		
		ActorRef observer1 = new JavaTestKit(system).getRef();
		ActorRef observer2 = new JavaTestKit(system).getRef();
		JavaTestKit probe1 = new JavaTestKit(system);
		JavaTestKit probe2 = new JavaTestKit(system);
		ActorRef subscriber1 = probe1.getRef();
		ActorRef subscriber2 = probe2.getRef();
		ActorBusImpl actorBus = new ActorBusImpl(system);
	
		actorBus.subscribe(subscriber1, observer1);
		actorBus.subscribe(subscriber2, observer1);
		actorBus.subscribe(subscriber2, observer2);
		
		Notification n1 = new Notification(observer1, 100);
		actorBus.publish(n1);
		System.out.println("prob1 expect msg equals n1: " + probe1.expectMsgEquals(n1));
		System.out.println("prob2 expect msg equals n1: " + probe2.expectMsgEquals(n1));
		
		Notification n2 = new Notification(observer2, 101);
		actorBus.publish(n2);
		System.out.println("prob2 expect msg equals n2: " + probe2.expectMsgEquals(n2));
		probe1.expectNoMsg(FiniteDuration.create(500, TimeUnit.MILLISECONDS));
		System.out.println("prob1 no expect msg");
	}
}