package br.edu.ufcg.ic.akka.java.routing;

import java.util.ArrayList;
import java.util.List;

import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.RoutingLogic;
import akka.routing.SeveralRoutees;
import scala.collection.immutable.IndexedSeq;

public class RedundancyRoutingLogic implements RoutingLogic {
	private final int nbrCopies;

	public RedundancyRoutingLogic(int nbrCopies) {
		this.nbrCopies = nbrCopies;
	}

	RoundRobinRoutingLogic roundRobin = new RoundRobinRoutingLogic();

	@Override
	public Routee select(Object message, IndexedSeq<Routee> routees) {
		List<Routee> targets = new ArrayList<Routee>();
		for (int i = 0; i < nbrCopies; i++) {
			targets.add(roundRobin.select(message, routees));
		}
		return new SeveralRoutees(targets);
	}

}
