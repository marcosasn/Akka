package br.edu.ufcg.ic.akka.java.faulttolerance;

import akka.actor.ActorSystem;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import scala.concurrent.duration.Duration;

public class FaultHandlingTest {
	static ActorSystem system;
	Duration timeout = Duration.create(5, "SECONDS");
	
	@BeforeClass
	public static void start() {
		system = ActorSystem.create("FaultHandlingTest");
	}

	@AfterClass
	public static void cleanup() {
		system.shutdown();
	}

	@Test
	public void mustEmploySupervisorStrategy() throws Exception {
		// code here
	}
}