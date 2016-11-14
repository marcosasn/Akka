package br.edu.ufcg.ic.akka.java.routing;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.routing.Routee;
import akka.routing.SeveralRoutees;
import scala.collection.immutable.IndexedSeq;

import akka.japi.Util;

final class TestRoutee implements Routee {
	public final int n;

	public TestRoutee(int n) {
		this.n = n;
	}

	@Override
	public void send(Object message, ActorRef sender) {
	}

	@Override
	public int hashCode() {
		return n;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof TestRoutee) && n == ((TestRoutee) obj).n;
	}
}

public class RedundancyTest{
	
	@BeforeClass
	public static void start() throws Exception {
		
	}

	@AfterClass
	public static void cleanup() {
	
	}
	
	@Test
	public void mustEmploySupervisorStrategyResume() throws Exception {
		RedundancyRoutingLogic logic = new RedundancyRoutingLogic(3);
		List<Routee> routeeList = new ArrayList<Routee>();
		for(int n = 1;n<=7;n++){
			routeeList.add(new TestRoutee(n));
		}
		IndexedSeq<Routee> routees = Util.immutableIndexedSeq(routeeList);
		SeveralRoutees r1 = (SeveralRoutees) logic.select("msg", routees);
		assertEquals(r1.getRoutees().get(0), routeeList.get(0));
		assertEquals(r1.getRoutees().get(1), routeeList.get(1));
		assertEquals(r1.getRoutees().get(2), routeeList.get(2));
		
		SeveralRoutees r2 = (SeveralRoutees) logic.select("msg", routees);
		assertEquals(r2.getRoutees().get(0), routeeList.get(3));
		assertEquals(r2.getRoutees().get(1), routeeList.get(4));
		assertEquals(r2.getRoutees().get(2), routeeList.get(5));
		
		SeveralRoutees r3 = (SeveralRoutees) logic.select("msg", routees);
		assertEquals(r3.getRoutees().get(0), routeeList.get(6));
		assertEquals(r3.getRoutees().get(1), routeeList.get(0));
		assertEquals(r3.getRoutees().get(2), routeeList.get(1));
	}
}