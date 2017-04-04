package br.edu.ufcg.ic.akka.eventbus;

import akka.actor.ActorRef;
import akka.event.japi.LookupEventBus;
import br.edu.ufcg.ic.akka.eventbus.LookupBusImpl.MsgEnvelope;

/***Publishes the payload of the MsgEnvelope when the topic of the MsgEnvelope equals
the String specified when subscribing.*/
public class LookupBusImpl extends LookupEventBus<MsgEnvelope, ActorRef, String> {
	
	public class MsgEnvelope {
		public final String topic;
		public final Object payload;

		public MsgEnvelope(String topic, Object payload) {
			this.topic = topic;
			this.payload = payload;
		}
	}
	
	// is used for extracting the classifier from the incoming events
	@Override
	public String classify(MsgEnvelope event) {
		return event.topic;
	}

	// will be invoked for each event for all subscribers which registered
	// themselves for the event’s classifier
	@Override
	public void publish(MsgEnvelope event, ActorRef subscriber) {
		subscriber.tell(event.payload, ActorRef.noSender());
	}

	// must define a full order over the subscribers, expressed as expected
	// from `java.lang.Comparable.compare`
	@Override
	public int compareSubscribers(ActorRef a, ActorRef b) {
		return a.compareTo(b);
	}

	// determines the initial size of the index data structure
	// used internally (i.e. the expected number of different classifiers)
	@Override
	public int mapSize() {
		return 128;
	}
	
	public void main(String[] args){
		LookupBusImpl lookupBus = new LookupBusImpl();
		//lookupBus.subscribe(getTestActor(), "greetings");
		lookupBus.publish(new MsgEnvelope("time", System.currentTimeMillis()));
		lookupBus.publish(new MsgEnvelope("greetings", "hello"));
		//expectMsgEquals("hello");
		//441
	}
}