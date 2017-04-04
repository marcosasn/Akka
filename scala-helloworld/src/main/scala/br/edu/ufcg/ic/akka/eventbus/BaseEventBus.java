package br.edu.ufcg.ic.akka.eventbus;

import akka.actor.FSM.Event;
import scala.collection.mutable.Subscriber;
import scala.concurrent.duration.DurationConversions.Classifier;

public interface BaseEventBus {
	/**
	* Attempts to register the subscriber to the specified Classifier
	* @return true if successful and false if not (because it was already
	* subscribed to that Classifier, or otherwise)
	*/
	public boolean subscribe(Subscriber subscriber, Classifier to);
	/**
	* Attempts to deregister the subscriber from the specified Classifier
	* @return true if successful and false if not (because it wasn't subscribed
	* to that Classifier, or otherwise)
	*/
	public boolean unsubscribe(Subscriber subscriber, Classifier from);
	/**
	* Attempts to deregister the subscriber from all Classifiers it may be subscribed to
	*/
	public void unsubscribe(Subscriber subscriber);
	/**
	* Publishes the specified Event to this bus
	*/
	public void publish(Event event);
}
