package br.edu.ufcg.ic.akka.eventbus;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.Creator;
import br.edu.ufcg.ic.akka.eventbus.Channel.Input;
import br.edu.ufcg.ic.akka.eventbus.Channel.Output;

public class Reader extends BaseOut {

	static public interface Message {}
	static public abstract class Command implements Message {}
	static public class Start extends Command {	}

	private static ActorSystem system;
	
	public Reader(ActorSystem system) {
		super();
		Reader.system = system;
	}

	public static Props props() {
		return Props.create(new Creator<Reader>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Reader create() throws Exception {
				return new Reader(system);
			}

		});
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if (getState() == State.state_output) {
			if (message instanceof Input) {
				int valor = ((Input) message).getValor();
				System.out.println("Leitor " + getSelf().path().name() + " leu valor " + valor);
				transition(getState(), message);
				
			} else if (message instanceof Start) {
				syso("reader: sending read channel request");
				transition(getState(), message);
			
				if (system != null) {
					system.eventStream().publish(new Output(2), getSelf());
				}
				
			} else {
				syso(message.toString());
			}
		}
	}

	@Override
	protected void transition(State old, Object event) {
		if (old == State.state_output && event instanceof Start) {
			setState(State.state_output);
		} else if (old == State.state_output && event instanceof Input) {
			setState(State.state_output);
		}
	}
}
