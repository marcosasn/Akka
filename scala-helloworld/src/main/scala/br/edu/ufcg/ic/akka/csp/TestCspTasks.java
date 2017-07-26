package br.edu.ufcg.ic.akka.csp;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.Procedure;
import br.edu.ufcg.ic.akka.csp.event.Event;
import br.edu.ufcg.ic.akka.csp.event.Tick;
import br.edu.ufcg.ic.akka.csp.event.TypedEvent;
import br.edu.ufcg.ic.akka.csp.process.ProcessCSP;
import br.edu.ufcg.ic.akka.csp.process.Skip;
import br.edu.ufcg.ic.akka.csp.process.Stop;
import br.edu.ufcg.ic.akka.csp.process.ProcessCSP.ProcessCSPApi.AddInitial;
import br.edu.ufcg.ic.akka.csp.process.ProcessCSP.ProcessCSPApi.Execute;
import br.edu.ufcg.ic.akka.csp.process.ProcessCSP.ProcessCSPApi.SetBehavior;


public class TestCspTasks {

	public static void main(String[] args) throws InterruptedException {
		Config config = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", config.getConfig("akka.actor"));

		/* STOP */
		ActorRef stop = system.actorOf(Props.create(Stop.class), "stop");
		/* SKIP */
		ActorRef skip = system.actorOf(Props.create(Skip.class), "skip");
		system.eventStream().subscribe(stop, Event.class);
		system.eventStream().subscribe(skip, Event.class);
		
		Event ev = new TypedEvent<String>("hello");
		system.eventStream().publish(ev);
		system.eventStream().publish(new Tick());

		/* a->STOP */
		ActorRef p1 = system.actorOf(Props.create(ProcessCSP.class), "p1");
		Event initial = new TypedEvent<String>("a");
		p1.tell(new AddInitial(initial), ActorRef.noSender());
		p1.tell(new SetBehavior(
				new Procedure<Object>() {
					@Override
					public void apply(Object message) {
						System.out.println("deadlock......");
					}
										}), ActorRef.noSender());

		system.eventStream().subscribe(p1, Event.class);
		system.eventStream().publish(initial);


		/* a->STOP */
		ActorRef p2 = system.actorOf(Props.create(ProcessCSP.class), "p2");
		p2.tell(new AddInitial(initial), ActorRef.noSender());
		p2.tell(new SetBehavior(
				new Procedure<Object>() {
					@Override
					public void apply(Object message) {
						System.out.println("deadlock......");
					}
										}), ActorRef.noSender());
		p2.tell(new Execute(), ActorRef.noSender());

		/* a->(a->(STOP)) */
		/*List<String> initials2 = new ArrayList<String>();
		initials2.add("a");
		initials2.add("a");
		ActorRef p3 = system.actorOf(Props.create(ProcessCSP.class, initials2), "p3");
		scanningBus.subscribe(p3, 3);
		scanningBus.publish("a");
		scanningBus.publish("a");*/

		/*
		 * Testando operador de prefixo a->STOP || a->STOP
		 */
		/*
		 * ActorRef spc =
		 * system.actorOf(Props.create(SynchronousParallelComposition.class,
		 * scanningBus), "spc"); scanningBus.subscribe(spc, 3);
		 * scanningBus.publish("a");
		 */

		/* a -> b -> SKIP || c -> STOP = isso deve ser igual a STOP */
		/*
		 * String[] initialsP1 = new String[]{"a","b"}; String[] initialsP2 =
		 * new String[]{"c"}; ActorRef spc2 =
		 * system.actorOf(Props.create(SynchronousParallelComposition.class,
		 * scanningBus), "spc2"); scanningBus.subscribe(spc, 3);
		 */
	}
}
