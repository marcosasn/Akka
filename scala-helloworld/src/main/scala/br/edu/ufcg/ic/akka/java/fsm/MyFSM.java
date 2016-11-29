package br.edu.ufcg.ic.akka.java.fsm;

import java.util.List;

import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import br.edu.ufcg.ic.akka.java.fsm.MyFSM.MyFSMApi.Queue;
import br.edu.ufcg.ic.akka.java.fsm.MyFSM.MyFSMApi.SetTarget;
import br.edu.ufcg.ic.akka.java.fsm.MyFSM.MyFSMApi.Batch;

public class MyFSM extends MyFSMBase {
	
	public interface MyFSMApi {
		public final class SetTarget {
			final ActorRef ref;
	
			public SetTarget(ActorRef ref) {
				this.ref = ref;
			}
		}
	
		public final class Queue {
			final Object o;
	
			public Queue(Object o) {
				this.o = o;
			}
		}
	
		public static final Object flush = new Object();
	
		public final class Batch {
			final List<Object> objects;
			
			public Batch(List<Object> objects) {
				this.objects = objects;
			}
		}
	}

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	protected void transition(State old, State next) {
		if (old == State.ACTIVE) {
			getTarget().tell(new Batch(drainQueue()), getSelf());
		}
	}

	@Override
	public void onReceive(Object o) throws Throwable {
		if (getState() == State.IDLE) {
			if (o instanceof SetTarget){
				init(((SetTarget) o).ref);
				log.info("SetTarget recebido, inciando FSM");
			}
			else
				whenUnhandled(o);
		} else if (getState() == State.ACTIVE) {
			if (o == MyFSMApi.flush) {
				setState(State.IDLE);
				log.info("Flush recebido, FSM estado IDLE");
			} else
				whenUnhandled(o);
		}
	}

	private void whenUnhandled(Object o) {
		if (o instanceof Queue && isInitialized()) {
			enqueue(((Queue) o).o);
			setState(State.ACTIVE);
			log.info("fila recebida, FSM estado ACTIVE");
		} else {
			log.warning("received unknown message {} in state {}", o, getState());
		}
	}
}
