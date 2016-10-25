package br.edu.ufcg.ic.akka.java.faulttolerance;

import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.actor.*;
import akka.japi.Function;
import static akka.actor.SupervisorStrategy.resume;
import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.stop;
import static akka.actor.SupervisorStrategy.escalate;

import scala.Option;
import scala.concurrent.duration.Duration;

public class Supervisor2 extends UntypedActor {
	private static SupervisorStrategy strategy = new OneForOneStrategy(10, Duration.create("1 minute"),
			new Function<Throwable, Directive>() {
				@Override
				public Directive apply(Throwable t) {
					if (t instanceof ArithmeticException) {
						return resume();
					} else if (t instanceof NullPointerException) {
						return restart();
					} else if (t instanceof IllegalArgumentException) {
						return stop();
					} else {
						return escalate();
					}
				}
			});

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}

	public void onReceive(Object o) {
		if (o instanceof Props) {
			getSender().tell(getContext().actorOf((Props) o), getSelf());
		} else {
			unhandled(o);
		}
	}
	
	@Override
	public void preRestart(Throwable cause, Option<Object> msg) {
		// do not kill all children, which is the default here
	}
}