package br.edu.ufcg.ic.akka.java.faulttolerance;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.testkit.JavaTestKit;
import akka.testkit.TestProbe;

import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import static akka.pattern.Patterns.ask;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class FaultHandlingTest {
	static ActorSystem system;
	static Duration timeout = Duration.create(5, TimeUnit.SECONDS);
	static Props superprops;
	static ActorRef supervisor;
	static ActorRef child;

	@BeforeClass
	public static void start() throws Exception {
		//Let us create actors
		system = ActorSystem.create("FaultHandlingTest");
		superprops = Props.create(Supervisor.class);
		supervisor = system.actorOf(superprops, "supervisor");
		child = (ActorRef) Await.result(ask(supervisor, Props.create(Child.class), 5000), timeout);
	}

	@AfterClass
	public static void cleanup() {
		JavaTestKit.shutdownActorSystem(system);
		system = null;
	}

	@Test
	public void mustEmploySupervisorStrategy() throws Exception {
		// code here
	}

	@Test
	public void mustEmploySupervisorStrategyResume() throws Exception {
		//Resume directive
		child.tell(42, ActorRef.noSender());
		assert Await.result(ask(child, "get", 5000), timeout).equals(42);
		child.tell(new ArithmeticException(), ActorRef.noSender());
		assert Await.result(ask(child, "get", 5000), timeout).equals(42);
	}

	@Test
	public void mustEmploySupervisorStrategyNullPointerException() throws Exception {
		//NullPointerException directive
		child.tell(new NullPointerException(), ActorRef.noSender());
		assert Await.result(ask(child, "get", 5000), timeout).equals(0);
	}

	@Test
	public void mustEmploySupervisorStrategyIllegalArgumentException() throws Exception {
		// IllegalArgumentException directive
		final TestProbe probe = new TestProbe(system); 
		probe.watch(child);
		child.tell(new IllegalArgumentException(), ActorRef.noSender());
		probe.expectMsgClass(Terminated.class);
	}

	@Test
	public void mustEmploySupervisorStrategyException() throws Exception {
		// Exception directive
		final TestProbe probe = new TestProbe(system); 
		probe.watch(child);
		child = (ActorRef) Await.result(ask(supervisor, Props.create(Child.class), 5000), timeout);
		probe.watch(child);
		assert Await.result(ask(child, "get", 5000), timeout).equals(0);
		child.tell(new Exception(), ActorRef.noSender());
		probe.expectMsgClass(Terminated.class);
	}
	
	@Test
	public void mustEmploySupervisor2Strategy() throws Exception {
		// Supervisor2 strategy
		superprops = Props.create(Supervisor2.class);
		supervisor = system.actorOf(superprops);
		child = (ActorRef) Await.result(ask(supervisor,Props.create(Child.class), 5000), timeout);
		child.tell(23, ActorRef.noSender());
		assert Await.result(ask(child, "get", 5000), timeout).equals(23);
		child.tell(new Exception(), ActorRef.noSender());
		assert Await.result(ask(child, "get", 5000), timeout).equals(0);
	}
}