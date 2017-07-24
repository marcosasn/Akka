package br.edu.ufcg.ic.akka.eventbus;

import akka.actor.ActorRef;
import akka.event.japi.LookupEventBus;
import br.edu.ufcg.ic.akka.eventbus.LookupBusImpl.MsgEnvelope;
import scala.collection.mutable.Subscriber;
import scala.concurrent.duration.DurationConversions.Classifier;

/***Publishes String messages with length less than or equal to the length*specified when subscribing.*/

public class TypedBus<S> extends LookupEventBus<MsgEnvelope, ActorRef> {

	public static class MsgEnvelope {
		public final S topic;
		public final Object payload;

		public MsgEnvelope(S topic, Object payload) {
			this.topic = topic;
			this.payload = payload;
		}
	}
	
	@Override
	public S classify() {
		return S;
	}

	@Override
	public int compareSubscribers(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int mapSize() {
		return 128;
	}
	
	@Override
	public void publish(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}


}