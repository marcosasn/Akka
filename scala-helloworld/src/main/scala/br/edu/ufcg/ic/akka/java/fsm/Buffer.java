package br.edu.ufcg.ic.akka.java.fsm;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Buffer extends BaseBuffer {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	@Override
	protected void transition(State old, String event, State next) {
		//TODO
		if (old == State.SIZE_0 && event.equals("input")) {
			setState(next);
		}
	}

	@Override
	public void onReceive(Object o) throws Throwable {
		if (getState() == State.SIZE_0) {
			if (o.equals("input")){
				transition(State.SIZE_0,"input",State.SIZE_1);
				log.info("input recebido em estado " + getState());
			}
			else
				whenUnhandled(o);
		} else if (getState() == State.SIZE_1) {
			if (o.equals("input")){
				transition(State.SIZE_1,"input",State.SIZE_2);
				log.info("input recebido em estado " + getState());
			}
			else if (o.equals("output")){
				transition(State.SIZE_1,"output",State.SIZE_0);
				log.info("output recebido em estado " + getState());
			}
			else 
				whenUnhandled(o);
		} else if (getState() == State.SIZE_2) {
			if (o.equals("output")){
				transition(State.SIZE_2,"output",State.SIZE_1);
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
		ActorRef p = system.actorOf(Props.create(Buffer.class), "p");
		
		p.tell("new SetTarget(target)", ActorRef.noSender());	
		p.tell("output", ActorRef.noSender());
		p.tell("output", ActorRef.noSender());

	}
}