package br.edu.ufcg.ic.akka.java.fsm;


import javax.swing.event.ChangeEvent;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import br.edu.ufcg.ic.akka.java.faulttolerance.Buffer.BufferApi.Input;
import br.edu.ufcg.ic.akka.java.faulttolerance.Buffer.BufferApi.Output;
import br.edu.ufcg.ic.akka.java.faulttolerance.Buffer.BufferApi.BufferException;
import br.edu.ufcg.ic.akka.java.faulttolerance.Buffer.BufferApi.GenerateBufferFailure;
import br.edu.ufcg.ic.swing.ListenerBuffer;

public class Buffer extends BaseBuffer {

	public interface BufferApi {

		public static class BufferException extends RuntimeException {
			private static final long serialVersionUID = 1L;

			public BufferException(String msg) {
				super(msg);
			}
		}

		public static class Input {
			private final Integer numero;

			public Input(Integer numero) {
				this.numero = numero;
			}

			public Integer getNumero() {
				return numero;
			}
		}

		public static class Output {
			public Output() {
			}
		}

		public static class Full {
			private int input;

			public Full(int input) {
				this.input = input;
			}

			public int getInput() {
				return input;
			}

			public void setInput(int input) {
				this.input = input;
			}
		}

		public static class Empty {
			public Empty() {
			}
		}

		public static class GenerateBufferFailure {
			public GenerateBufferFailure() {
			}
		}
	}

	private LoggingAdapter log;
	private ActorRef produtor;
	private ActorRef consumidor;
	private static ListenerBuffer listener;

	public static Props props() {
		return Props.create(new Creator<Buffer>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Buffer create() throws Exception {
				return new Buffer(listener);
			}

		});
	}

	public Buffer(ListenerBuffer listenerBuffer) {
		this.log = Logging.getLogger(getContext().system(), this);
		if (listenerBuffer != null) {
			listener = listenerBuffer;
		}
		init();
	}

	private void fireChangeEventPerformed() {
		ChangeEvent changeEvent = new ChangeEvent(getNumbers());
		listener.stateChanged(changeEvent);
	}

	private void generateFailure() {
		throw new BufferException("Simulated buffer failure");
	}

	public void onReceive(Object message) throws BufferException {
		if (getState() == State.SIZE_0) {
			if (message instanceof Input) {
				produtor = getSender();
				int numeroRecebido = ((Input) message).getNumero();

				addNumber(numeroRecebido);
				transition(State.SIZE_0, message);
				log.info("Add int : " + numeroRecebido + " from : " + produtor);
				fireChangeEventPerformed();
				
			} else if (message instanceof GenerateBufferFailure) {
				generateFailure();
			} else {
				whenUnhandled(message);
			}

		} else if (getState() == State.SIZE_1) {
			if (message instanceof Input) {
				produtor = getSender();
				int numeroRecebido = ((Input) message).getNumero();
				addNumber(numeroRecebido);
				transition(State.SIZE_1, message);
				log.info("Add int : " + numeroRecebido + " from : " + produtor);
				fireChangeEventPerformed();
				
			} else if (message instanceof Output) {
				consumidor = getSender();
				int aux = removeFirst();
				transition(State.SIZE_1, message);
				log.info("Removido int : " + aux + " from : " + getSender());
				consumidor.tell(new BufferApi.Input(aux), getSelf());
				fireChangeEventPerformed();
				
			} else if (message instanceof GenerateBufferFailure) {
				generateFailure();
			} else
				whenUnhandled(message);
			
		} else if (getState() == State.SIZE_2) {
			if (message instanceof Output) {
				consumidor = getSender();
				int aux = removeFirst();
				transition(State.SIZE_2, message);
				log.info("Removido int : " + aux + " from : " + getSender());
				consumidor.tell(new BufferApi.Input(aux), getSelf());
				fireChangeEventPerformed();
				
			} else if (message instanceof GenerateBufferFailure) {
				generateFailure();
			} else
				whenUnhandled(message);
		}
	}

	@Override
	protected void transition(State old, Object message) {
		if (old == State.SIZE_0 && message instanceof Input){
			setState(State.SIZE_1);
		}
		else if (old == State.SIZE_1){
			if(message instanceof Input){
				setState(State.SIZE_2);
			} else if (message instanceof Output){
				setState(State.SIZE_0);
			}
		}
		else if (old == State.SIZE_2 && message instanceof Output) {
			setState(State.SIZE_1);
		}
	}

	private void whenUnhandled(Object o) {
		log.warning("received unknown message {} in state {}", o, getState());
	}
}