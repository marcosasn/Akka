package br.edu.ufcg.ic.akka.channel;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Channel extends UntypedActor {

	//por enquanto os interessados nesse evento vao se inscrever no canal como leitores
	private List<ActorRef> leitores = new ArrayList<ActorRef>();
	
	static public class InputEvent { //alguem escreveu no canal    
		private int valor;
		public InputEvent(int valor) {
			this.valor= valor;
		}
		public int getValor() {
			return valor;
		}
		public void setValor(int valor) {
			this.valor = valor;
		}
		
    }
	static public class OutputEvent { //alguem escreveu no canal    
		private int valor;
		public OutputEvent(int valor) {
			this.valor= valor;
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
		if(message instanceof InputEvent){
			//se alguem escreveu no canal entao tem que avisar a todos os leitores (ou pegar os 
			//leitores desse evento em algum lugar e avisar a eles)
			int valor = ((InputEvent) message).getValor();
			System.out.println("channel: input event received");
			leitores.stream().forEach(ar -> ar.tell(new OutputEvent(valor), getSelf()));
			leitores.clear();
		} else if (message instanceof OutputEvent){
			//quem mandar mensagem de output precisa ficar esperando ate que um input venha
			//assim, essa mensagem coloca os leitores na fila de espera e nao manda resposta 
			//para eles
			System.out.println("channel: read channel request received. waiting for one write/input event");
			leitores.add(getSender());
		}
	}

}
