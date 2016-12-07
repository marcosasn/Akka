package br.edu.ufcg.ic.akka.java.fsm;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import br.edu.ufcg.ic.akka.java.fsm.FSM.FSMApi.UP;
import br.edu.ufcg.ic.akka.java.fsm.MyFSM.MyFSMApi.Queue;
import br.edu.ufcg.ic.akka.java.fsm.MyFSM.MyFSMApi.SetTarget;
import br.edu.ufcg.ic.akka.java.MyUntypedActor;
import br.edu.ufcg.ic.akka.java.fsm.FSM.FSMApi.DOWN;

public class FSM extends FSMBase {
	
	public interface FSMApi {
		
		public final class UP {
	
			public UP() {	}
		}
		
		public final class DOWN {
			
			public DOWN() {	}
		}
	}

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	protected void transition(State old, String event, State next) {
		if (old == State.UP && event.equals("up")) {
			up();
			setState(State.DOWN);
		}else if (old == State.DOWN  && event.equals("down")) {
			down();
			setState(State.UP);
		}
	}

	@Override
	public void onReceive(Object o) throws Throwable {
		if (getState() == State.UP) {
			if (o instanceof UP){
				transition(State.UP,"up",State.DOWN);
			}
			else
				whenUnhandled(o);
		} else if (getState() == State.DOWN) {
			if (o instanceof DOWN) {
				transition(State.DOWN,"down",State.UP);
			} else
				whenUnhandled(o);
		}
	}

	private void whenUnhandled(Object o) {
		log.warning("received unknown message {} in state {}", o, getState());
	}
	
	private void up(){
		log.info("up... state: " + getState());
	}
	
	private void down(){
		log.info("down... state: " + getState());
	}
	
	public static void main(String[] args) {
		Config config = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("MySystem", config.getConfig("akka.actor"));
		ActorRef fsm = system.actorOf(Props.create(FSM.class), "fsm");
		
		fsm.tell("new SetTarget(target)", ActorRef.noSender());	
		fsm.tell(new UP(), ActorRef.noSender());
		fsm.tell(new UP(), ActorRef.noSender());
		fsm.tell(new DOWN(), ActorRef.noSender());
		fsm.tell(new DOWN(), ActorRef.noSender());
	}
}
