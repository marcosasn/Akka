package br.edu.ufcg.ic.akka.java;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Stash;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;

public class Buffer extends UntypedActor{
	private LoggingAdapter log;
	private List<Integer> numeros;
	private static Integer tamanho;
	private ActorRef produtor;
	private ActorRef consumidor;
	
	static public class Input {
        private final Integer numero;
        
        public Input(Integer numero) {
            this.numero = numero;
        }

        public Integer getNumero() {
    		return numero;
        }
    }
	
	static public class Output {    
        public Output() {}
    }
	
	static public class Full {    
        public Full() {}
    }
	
	static public class Empty {    
        public Empty() {}
    }
	
	public static Props props() {
        return Props.create(new Creator<Buffer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Buffer create() throws Exception {
                return new Buffer(tamanho);
            }

        });
    }
	
	public Buffer(int tamanho) {
		this.log = Logging.getLogger(getContext().system(), this);
    	this.numeros = new ArrayList<>();
    	Buffer.tamanho = tamanho;
    }
	
    public void onReceive(Object message) throws Exception {
        if (message instanceof Input) {
        	produtor = getSender();
            if(numeros.size() < tamanho) {
            	numeros.add(((Input)message).getNumero());
            	log.info("Add int : " + ((Input)message).getNumero() + " from : " + getSender());
            	produtor.tell(new Produtor.Produzir(), getSelf());
            } else {
            	produtor.tell(new Buffer.Full(), getSelf());
            }
        } else if (message instanceof Output){
        	consumidor = getSender();
            if(numeros.size() > 0) {
            	int aux = numeros.remove(numeros.size() - 1);
            	log.info("Removido int : " + aux + " from : " + getSender());
            	consumidor.tell(aux, getSelf());
            	produtor.tell(new Produtor.Produzir(), getSelf());
            } else {
            	consumidor.tell(new Empty(), getSelf());
            }
        } else 
        	unhandled(message);
    }
}


