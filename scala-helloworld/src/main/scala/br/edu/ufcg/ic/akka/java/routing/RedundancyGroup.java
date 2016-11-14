package br.edu.ufcg.ic.akka.java.routing;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.dispatch.Dispatchers;
import akka.japi.Creator;
import akka.routing.Router;
import br.edu.ufcg.ic.akka.java.faulttolerance.FaultHandlingDocSample.Storage;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.routing.ActorRefRoutee;
import akka.routing.FromConfig;
import akka.routing.GroupBase;

public class RedundancyGroup extends GroupBase {
	private final List<String> paths;
	private final int nbrCopies;

	public RedundancyGroup(List<String> paths, int nbrCopies) {
		this.paths = paths;
		this.nbrCopies = nbrCopies;
	}

	public RedundancyGroup(Config config) {
		this(config.getStringList("routees.paths"), config.getInt("nbr-copies"));
	}

	@Override
	public java.lang.Iterable<String> getPaths(ActorSystem system) {
		return paths;
	}

	@Override
	public Router createRouter(ActorSystem system) {
		return new Router(new RedundancyRoutingLogic(nbrCopies));
	}

	@Override
	public String routerDispatcher() {
		return Dispatchers.DefaultDispatcherId();
	}

	public static void main(String[] args) {
		Config conf = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", conf.getConfig("akka.actor"));
		
		for (int n = 1; n <= 10; n++) {
			system.actorOf(Props.create(Storage.class), "s" + n);
		}
		List<String> paths = new ArrayList<String>();
		for (int n = 1; n <= 10; n++) {
			paths.add("/user/s" + n);
		}
		ActorRef redundancy1 = system.actorOf(new RedundancyGroup(paths, 3).props(), "redundancy1");
		redundancy1.tell("important", ActorRef.noSender());
		
		ActorRef redundancy2 = system.actorOf(new RedundancyGroup(conf).props(), "redundancy2");
		//redundancy2.tell("very important", ActorRef.noSender());
		
		//ActorRef redundancy2 = system.actorOf(new RedundancyGroup(conf.getConfig("akka.actor")).props(), "redundancy2");
	}
}