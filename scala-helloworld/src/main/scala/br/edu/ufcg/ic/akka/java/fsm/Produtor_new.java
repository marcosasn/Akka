package br.edu.ufcg.ic.akka.java.fsm;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Produtor_new extends BaseProdutor {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	@Override
	protected void transition(State old, String event) {
		if (old == State.INPUT && event.equals("input")) {
			setState(State.INPUT);
		}
	}

	@Override
	public void onReceive(Object o) throws Throwable {
		if (getState() == State.INPUT) {
			if (o.equals("input")){
				transition(State.INPUT,"input");
				log.info("input recebido em estado " + getState());
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
		ActorRef p = system.actorOf(Props.create(Produtor_new.class), "p");
		
		p.tell("new SetTarget(target)", ActorRef.noSender());	
		p.tell("input", ActorRef.noSender());
		p.tell("input", ActorRef.noSender());

	}
}