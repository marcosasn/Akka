package br.edu.ufcg.ic.akka.eventbus;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.DeadLetter;
import akka.actor.Props;
import akka.actor.UntypedActor;

interface AllKindsOfMusic {}

class Jazz implements AllKindsOfMusic {
	final public String artist;

	public Jazz(String artist) {
		this.artist = artist;
	}
	
	public String toString(){
		return "Jazz";
	}
}

class Electronic implements AllKindsOfMusic {
	final public String artist;

	public Electronic(String artist) {
		this.artist = artist;
	}
	
	public String toString(){
		return "Eletronic";
	}
}

class Listener extends UntypedActor {
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Jazz) {
			System.out.printf("%s is listening to: %s%n", self().path().name(), message);
		} else if (message instanceof Electronic) {
			System.out.printf("%s is listening to: %s%n", self().path().name(), message);
		}
	}
}

public class SubscribeGroupEventExample {
	public static void main(String[] args) {
		Config config = ConfigFactory.load();
		final ActorSystem system = ActorSystem.create("system", config.getConfig("akka.actor"));
		
		final ActorRef actor = system.actorOf(Props.create(DeadLetterActor.class));
		system.eventStream().subscribe(actor, DeadLetter.class);
		
		final ActorRef jazzListener = system.actorOf(Props.create(Listener.class));
		final ActorRef musicListener = system.actorOf(Props.create(Listener.class));
		system.eventStream().subscribe(jazzListener, Jazz.class);
		system.eventStream().subscribe(musicListener, AllKindsOfMusic.class);
		
		// only musicListener gets this message, since it listens to *all* kinds of music:
		system.eventStream().publish(new Electronic("Parov Stelar"));
		// jazzListener and musicListener will be notified about Jazz:
		system.eventStream().publish(new Jazz("Sonny Rollins"));
	}
}
