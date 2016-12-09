package br.edu.ufcg.ic.akka.java.fsm;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Consumidor extends BaseConsumidor {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	@Override
	protected void transition(State old, String event, State next) {
		if (old == State.OUTPUT && event.equals("output")) {
			setState(next);
		}
	}

	@Override
	public void onReceive(Object o) throws Throwable {
		if (getState() == State.OUTPUT) {
			if (o.equals("output")){
				transition(State.OUTPUT,"output",State.OUTPUT);
				log.info("output recebido em estado " + getState());
			}
			else
				whenUnhandled(o);
		}
	}

	private void whenUnhandled(Object o) {
		log.warning("received unknown message {} in state {}", o, getState());
	}
	
	public static void main(String[] args) {
		Config config = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", config.getConfig("akka.actor"));
		ActorRef p = system.actorOf(Props.create(Consumidor.class), "p");
		
		p.tell("new SetTarget(target)", ActorRef.noSender());	
		p.tell("output", ActorRef.noSender());
		p.tell("output", ActorRef.noSender());

	}
}