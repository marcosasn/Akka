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
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private List<Integer> numeros;
	
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
	
	static public class IsFull {    
        public IsFull() {}
    }
	
	static public class IsEmpty {    
        public IsEmpty() {}
    }
	
	public static Props props() {
        return Props.create(new Creator<Buffer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Buffer create() throws Exception {
                return new Buffer();
            }

        });
    }
	
	public Buffer() {
    	this.numeros = new ArrayList<>();
    }
	
    public void onReceive(Object message) throws Exception {
        if (message instanceof Input) {
            log.info("Received input message: {}", message);
            if(numeros.size() < 5) {
            	numeros.add(((Input)message).getNumero());
            	log.info("Add input : {}", ((Input)message).getNumero());
            } else {
            	getSender().tell(new IsFull(), getSelf());
            	log.info("Buffer is full");
            }
        } else if (message instanceof Output){
        	log.info("Received output message: {}", message);
            if(numeros.size() > 0) {
            	int aux = numeros.get(numeros.size() - 1);
            	log.info("Pop output");
            	getSender().tell(aux, getSelf());
            } else {
            	getSender().tell(new IsEmpty(), getSelf());
            	log.info("Buffer is empty");
            }
        } else unhandled(message);
    }
}


