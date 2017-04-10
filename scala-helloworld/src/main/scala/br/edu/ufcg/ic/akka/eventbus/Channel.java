package br.edu.ufcg.ic.akka.eventbus;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.routing.Broadcast;
import akka.routing.BroadcastGroup;
import br.edu.ufcg.ic.akka.eventbus.Reader.Message;

public class Channel extends Base {

	private ActorRef router;
	private List<String> paths = new ArrayList<String>();
	
	static public abstract class Event implements Message {
		private int valor;

		public Event (int valor) {
			this.valor = valor;
		}
		
		public int getValor() {
			return valor;
		}

		public void setValor(int valor) {
			this.valor = valor;
		}
	}

	static public class Input extends Event { // alguem escreveu no canal
		
		public Input(int valor) {
			super(valor);
		}
	}

	static public class Output extends Event { // alguem escreveu no canal

		public Output(int valor) {
			super(valor);
		}
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if (getState() == State.state_input) {
			if (message instanceof Input) {
				int valor = ((Input) message).getValor();
				System.out.println("channel: input event received");
				router = getContext().actorOf(new BroadcastGroup(paths).props(), "router");
				router.tell(new Broadcast(new Channel.Output(valor)), getSelf());
				paths.clear();
				transition(getState(), message);
			} else if (message instanceof Output) {
				System.out.println("channel: read channel request received. waiting for one write/input event");
				paths.add("/user/" + getSender().path().name());
				transition(getState(), message);
			}
		} else if (getState() == State.state_output) {
			if (message instanceof Output) {
				System.out.println("channel: read channel request received. waiting for one write/input event");
				transition(getState(), message);
			} else {
				syso(message.toString());
			}
		}
	}

	@Override
	protected void transition(State old, Object event) {
		if (old == State.state_input && event instanceof Input) {
			setState(State.stop);
		} else if (old == State.state_output && event instanceof Output) {
			setState(State.state_input);
		} else if (old == State.state_input && event instanceof Output) {
			setState(State.state_input);
		}
	}
}