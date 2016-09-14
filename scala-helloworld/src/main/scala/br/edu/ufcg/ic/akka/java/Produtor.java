package br.edu.ufcg.ic.akka.java;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;

import br.edu.ufcg.ic.akka.java.Buffer.IsFull;

public class Produtor extends UntypedActor{
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private Integer producaoTotal = 10;
	private static ActorRef buffer;
	
	static public class Produzir {    
        public Produzir() {}
    }
	
	public static Props props() {
        return Props.create(new Creator<Produtor>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Produtor create() throws Exception {
                return new Produtor(buffer);
            }

        });
    }
	
	public Produtor(ActorRef buffer) {
    	Produtor.buffer = buffer;
    }
	
	public void produzir(){
		for(int i = 0; i < producaoTotal; i++){
			buffer.tell(new Buffer.Input(i), getSelf());
		}
	}
	
    public void onReceive(Object message) throws Exception {
        if (message instanceof Produzir){
        	produzir();
        }
		if (message instanceof IsFull) {
			
        } else unhandled(message);
    }

}
