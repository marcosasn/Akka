package br.edu.ufcg.ic.akka.eventbus;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.japi.ScanningEventBus;

/***Publishes String messages with length less than or equal to the length*specified when subscribing.*/

public class ScanningBusImpl extends ScanningEventBus<String, ActorRef, Integer> {
	// is needed for determining matching classifiers and storing them in an
	// ordered collection
	@Override
	public int compareClassifiers(Integer a, Integer b) {
		return a.compareTo(b);
	}

	// is needed for storing subscribers in an ordered collection
	@Override
	public int compareSubscribers(ActorRef a, ActorRef b) {
		return a.compareTo(b);
	}

	// determines whether a given classifier shall match a given event; it is
	// invoked
	// for each subscription for all received events, hence the name of the
	// classifier
	@Override
	public boolean matches(Integer classifier, String event) {
		return event.length() <= classifier;
	}

	// will be invoked for each event for all subscribers which registered
	// themselves
	// for the event’s classifier
	@Override
	public void publish(String event, ActorRef subscriber) {
		subscriber.tell(event, ActorRef.noSender());
	}
	
	public static void main(String[] args) {
		Config config = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", config.getConfig("akka.actor"));
		final ActorRef anyactor = system.actorOf(Props.create(AnyActor.class),"anyactor");
		ScanningBusImpl scanningBus = new ScanningBusImpl();
		
		scanningBus.subscribe(anyactor, 3);
		scanningBus.publish("xyzabc");
		scanningBus.publish("ab");
		//expectMsgEquals("ab");
		scanningBus.publish("abc");
		//expectMsgEquals("abc");
	}	
}