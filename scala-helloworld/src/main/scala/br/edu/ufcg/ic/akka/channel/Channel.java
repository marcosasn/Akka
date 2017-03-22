package br.edu.ufcg.ic.akka.channel;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.Broadcast;
import akka.routing.BroadcastGroup;
import akka.routing.BroadcastPool;

public class Channel extends Base {

	private ActorRef router;
	private List<String> paths = new ArrayList<String>();

	static public class InputEvent { // alguem escreveu no canal
		private int valor;

		public InputEvent(int valor) {
			this.valor = valor;
		}

		public int getValor() {
			return valor;
		}

		public void setValor(int valor) {
			this.valor = valor;
		}

	}

	static public class OutputEvent { // alguem escreveu no canal
		private int valor;

		public OutputEvent(int valor) {
			this.valor = valor;
		}

		public int getValor() {
			return valor;
		}

		public void setValor(int valor) {
			this.valor = valor;
		}
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if (getState() == State.state_input) {
			if (message instanceof InputEvent) {
				// se alguem escreveu no canal entao tem que avisar a todos os
				// leitores (ou pegar os
				// leitores desse evento em algum lugar e avisar a eles)
				int valor = ((InputEvent) message).getValor();
				System.out.println("channel: input event received");
				router = getContext().actorOf(new BroadcastGroup(paths).props(), "router");
				router.tell(new Broadcast(new Channel.OutputEvent(valor)), getSelf());
				transition(getState(), message);
			} else if (message instanceof OutputEvent) {
				// syso(message.toString());
				// if (message instanceof OutputEvent){
				// quem mandar mensagem de output precisa ficar esperando ate
				// que um input venha
				// assim, essa mensagem coloca os leitores na fila de espera e
				// nao manda resposta
				// para eles
				System.out.println("channel: read channel request received. waiting for one write/input event");
				paths.add("/user/" + getSender().path().name());
				transition(getState(), message);
			}
		} else if (getState() == State.state_output) {
			if (message instanceof OutputEvent) {
				// quem mandar mensagem de output precisa ficar esperando ate
				// que um input venha
				// assim, essa mensagem coloca os leitores na fila de espera e
				// nao manda resposta
				// para eles
				System.out.println("channel: read channel request received. waiting for one write/input event");
				transition(getState(), message);
			} else {
				syso(message.toString());
			}
		}
	}

	@Override
	protected void transition(State old, Object event) {
		if (old == State.state_input && event instanceof InputEvent) {
			//syso("input... state: " + getState());
			setState(State.stop);
		} else if (old == State.state_output && event instanceof OutputEvent) {
			//syso("output... state: " + getState());
			setState(State.state_input);
		} else if (old == State.state_input && event instanceof OutputEvent) {
			setState(State.state_input);
		}
	}
}