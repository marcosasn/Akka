package br.edu.ufcg.ic.akka.java.faulttolerance;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import br.edu.ufcg.ic.akka.java.faulttolerance.Buffer.BufferApi.Input;
import br.edu.ufcg.ic.akka.java.faulttolerance.Buffer.BufferApi.Output;
import br.edu.ufcg.ic.swing.ListenerBuffer;

public class Buffer extends UntypedActor{
	
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
	        public Output() {}
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
	        public Empty() {}
	    }		
	}
	
	private LoggingAdapter log;
	private List<Integer> numeros;
	private static Integer tamanho;
	private ActorRef produtor;
	private ActorRef consumidor;
	private static ListenerBuffer listener;		
	
	public static Props props() {
        return Props.create(new Creator<Buffer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Buffer create() throws Exception {
                return new Buffer(tamanho, listener);
            }

        });
    }
	
	public Buffer(int tamanho, ListenerBuffer listenerBuffer) {
		this.log = Logging.getLogger(getContext().system(), this);
    	this.numeros = new ArrayList<>();
    	Buffer.tamanho = tamanho;
    	if(listenerBuffer != null){
			listener = listenerBuffer;
		}
    }
	
	private void fireChangeEventPerformed() {
		ChangeEvent changeEvent = new ChangeEvent(numeros);
		listener.stateChanged(changeEvent);
	}
	
    public void onReceive(Object message) throws Exception {
        if (message instanceof Input) {
        	produtor = getSender();
        	int numeroRecebido = ((Input)message).getNumero();
        	if(numeros.size() < tamanho) {
            	numeros.add(numeroRecebido);
            	log.info("Add int : " + numeroRecebido + " from : " + getSender());
            	fireChangeEventPerformed();
            } else {
            	produtor.tell(new BufferApi.Full(numeroRecebido), getSelf());
            }
        } else if (message instanceof Output){
        	consumidor = getSender();
            if(numeros.size() > 0) {
            	int aux = numeros.remove(numeros.size() - 1);
            	//int aux = numeros.remove(0);
            	log.info("Removido int : " + aux + " from : " + getSender());
            	consumidor.tell(new BufferApi.Input(aux), getSelf());
            	fireChangeEventPerformed();
            	//produtor.tell(new Produtor.Produzir(), getSelf());
            } else {
            	consumidor.tell(new BufferApi.Empty(), getSelf());
            }
        } else 
        	unhandled(message);
    }
}