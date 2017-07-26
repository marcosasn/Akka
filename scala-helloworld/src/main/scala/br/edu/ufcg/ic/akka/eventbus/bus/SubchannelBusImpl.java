package br.edu.ufcg.ic.akka.eventbus.bus;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.japi.SubchannelEventBus;

import akka.util.Subclassification;
import br.edu.ufcg.ic.akka.eventbus.bus.LookupBusImpl.MsgEnvelope;

class StartsWithSubclassification implements Subclassification<String> {
	@Override
	public boolean isEqual(String x, String y) {
		return x.equals(y);
	}

	@Override
	public boolean isSubclass(String x, String y) {
		return x.startsWith(y);
	}
}

/***
 * Publishes the payload of the MsgEnvelope when the topic of the* MsgEnvelope
 * starts with the String specified when subscribing.
 */
public class SubchannelBusImpl extends SubchannelEventBus<MsgEnvelope, ActorRef, String> {
	// Subclassification is an object providing `isEqual` and `isSubclass`
	// to be consumed by the other methods of this classifier
	@Override
	public Subclassification<String> subclassification() {
		return new StartsWithSubclassification();
	}

	// is used for extracting the classifier from the incoming events
	@Override
	public String classify(MsgEnvelope event) {
		return event.topic;
	}

	// will be invoked for each event for all subscribers which registered
	// themselves
	// for the event’s classifier
	@Override
	public void publish(MsgEnvelope event, ActorRef subscriber) {
		subscriber.tell(event.payload, ActorRef.noSender());
	}

	public static void main(String[] args) {
		Config config = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", config.getConfig("akka.actor"));
		final ActorRef anyactor = system.actorOf(Props.create(AnyActor.class),"anyactor");

		SubchannelBusImpl subchannelBus = new SubchannelBusImpl();
		subchannelBus.subscribe(anyactor, "abc");
		subchannelBus.publish(new MsgEnvelope("xyzabc", "x"));
		subchannelBus.publish(new MsgEnvelope("bcdef", "b"));
		subchannelBus.publish(new MsgEnvelope("abc", "c"));
		//expectMsgEquals("c");
		subchannelBus.publish(new MsgEnvelope("abcdef", "d"));
		//expectMsgEquals("d");
		
	}
}
