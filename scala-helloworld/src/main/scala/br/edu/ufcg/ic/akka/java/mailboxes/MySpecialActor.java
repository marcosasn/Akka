package br.edu.ufcg.ic.akka.java.mailboxes;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.dispatch.RequiresMessageQueue;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import br.edu.ufcg.ic.akka.java.MyUntypedActor;

public class MySpecialActor extends MyUntypedActor implements RequiresMessageQueue<MyUnboundedJMessageQueueSemantics>{
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	@Override
	public void onReceive(Object message) {
		log.info(message.toString());
	}
	
	public static void main(String[] args) {
		Config conf = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", conf.getConfig("akka.actor"));
		// We create a new Actor that just prints out what it processes
		ActorRef x = system.actorOf(Props.create(MySpecialActor.class).withDispatcher("custom-dispatcher"),"x");
		for (Object msg : new Object[] { "foo", "bar", new MyControlMessage(),PoisonPill.getInstance() }) {
			x.tell(msg, ActorRef.noSender());
		}
		/*
		Logs:
		'MyControlMessage
		'foo
		'bar
		*/
	}
}