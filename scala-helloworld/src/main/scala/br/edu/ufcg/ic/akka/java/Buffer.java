package br.edu.ufcg.ic.akka.java;

import java.util.ArrayList;
import java.util.List;

import akka.actor.Props;
import akka.actor.Stash;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;

public class Buffer extends UntypedActor{
	LoggingAdapter log;
	private List<Integer> numeros;
	private static Integer tamanho;
	
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
		log = Logging.getLogger(getContext().system(), this);
    	this.numeros = new ArrayList<>();
    	Buffer.tamanho = tamanho;
    }
	
    public void onReceive(Object message) throws Exception {
        if (message instanceof Input) {
            if(numeros.size() < tamanho) {
            	numeros.add(((Input)message).getNumero());
            	log.info("Add int : {}", ((Input)message).getNumero());
            } else {
            	getSender().tell(new Full(), getSelf());
            	log.info("Buffer está cheio");
            }
        } else if (message instanceof Output){
            if(numeros.size() > 0) {
            	int aux = numeros.remove(numeros.size() - 1);
            	log.info("Removido int : {}", + aux);
            	getSender().tell(aux, getSelf());
            } else {
            	getSender().tell(new Empty(), getSelf());
            	log.info("Buffer está vazio");
            }
        } else unhandled(message);
    }
}


