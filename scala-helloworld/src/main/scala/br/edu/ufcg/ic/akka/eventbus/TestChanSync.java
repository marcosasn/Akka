package br.edu.ufcg.ic.akka.eventbus;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.Procedure;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.AddInitial;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.SetBehavior;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.Execute;


public class TestChanSync {

	public static void main(String[] args) throws InterruptedException {
		Config config = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", config.getConfig("akka.actor"));

		/* STOP */
		ActorRef stop = system.actorOf(Props.create(Stop.class), "stop");
		stop.tell("hello", ActorRef.noSender());
		stop.tell("hello2", ActorRef.noSender());
		stop.tell("hello3", ActorRef.noSender());

		/* SKIP */
		ActorRef skip = system.actorOf(Props.create(Skip.class), "skip");
		skip.tell("hello", ActorRef.noSender());
		skip.tell("tick", ActorRef.noSender());
		skip.tell("hello2", ActorRef.noSender());

		/* a->STOP */
		ScanningBusImpl scanningBus = new ScanningBusImpl();
		String initial = "a";
		ActorRef p1 = system.actorOf(Props.create(ProcessCSP.class), "p1");
		p1.tell(new AddInitial(initial), ActorRef.noSender());
		p1.tell(new SetBehavior(
				new Procedure<Object>() {
					@Override
					public void apply(Object message) {
						System.out.println("deadlock......");
					}
										}), ActorRef.noSender());

		scanningBus.subscribe(p1, 3);

		scanningBus.publish("a");
		scanningBus.publish("a");

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
