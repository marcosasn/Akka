package br.edu.ufcg.ic.akka.java;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import akka.util.Timeout;
import br.edu.ufcg.ic.akka.java.Buffer.Empty;
import br.edu.ufcg.ic.akka.java.Buffer.Input;

public class Consumidor extends UntypedActor {
	private LoggingAdapter log;
	private static ActorRef buffer;
	private boolean consumir;
	private long espera;

	static public class Consumir {    
        public Consumir() {}
    }
	
	static public class TempoEspera {
        private final int tempo;
        
        public TempoEspera(int tempo) {
            this.tempo = tempo;
        }

        public int getTempo() {
    		return tempo;
        }
    }
	
	public static Props props() {
        return Props.create(new Creator<Consumidor>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Consumidor create() throws Exception {
                return new Consumidor(buffer);
            }

        });
    }
	
	public Consumidor(ActorRef buffer) {
    	log = Logging.getLogger(getContext().system(), this);
    	Consumidor.buffer = buffer;
    	consumir = false;
    	espera = 0;
    }
	
	private void consumir(){
		while(consumir){
			try{
				Thread.sleep(espera);
			} catch (InterruptedException e){
				log.info(e.getMessage());
			}			
			buffer.tell(new Buffer.Output(), getSelf());
		}
	}
	
	public void onReceive(Object message) throws Exception {
        if (message instanceof Consumir){
        	consumir = true;
        	consumir();
        }
        else if (message instanceof Empty) {
			log.info("O buffer parece estar vazio...");
			consumir = false;
        } 
		else if(message instanceof Input){
			log.info("Consumidor recebeu um int... " + ((Input)message).getNumero());	
		} 
		else if (message instanceof TempoEspera) {
			espera = ((TempoEspera)message).getTempo();
		} else 
			unhandled(message);
    }
}