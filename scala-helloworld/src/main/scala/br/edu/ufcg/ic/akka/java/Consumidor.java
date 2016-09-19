package br.edu.ufcg.ic.akka.java;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import br.edu.ufcg.ic.akka.java.Buffer.Empty;

public class Consumidor extends UntypedActor {
	LoggingAdapter log;
	private static ActorRef buffer;
	private static Integer totalConsumir;

	
	static public class Consumir {    
        public Consumir() {}
    }
	
	public static Props props() {
        return Props.create(new Creator<Consumidor>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Consumidor create() throws Exception {
                return new Consumidor(buffer, totalConsumir);
            }

        });
    }
	
	public Consumidor(ActorRef buffer, int totalConsumir) {
    	log = Logging.getLogger(getContext().system(), this);
    	Consumidor.buffer = buffer;
    	Consumidor.totalConsumir = totalConsumir; 
    }
	
	public void consumir(){
		for(int i = 1; i <= totalConsumir; i++){
			buffer.tell(new Buffer.Output(), getSelf());
		}
	}
	
	public void onReceive(Object message) throws Exception {
        if (message instanceof Consumir){
        	consumir();
        }
        else if (message instanceof Empty) {
			//TO DO
			log.info("O buffer parece estar vazio...");

        } 
		else if(message instanceof Integer){
			log.info("Consumidor recebeu um int... " + (Integer)message);
			
		} else unhandled(message);
    }
}
