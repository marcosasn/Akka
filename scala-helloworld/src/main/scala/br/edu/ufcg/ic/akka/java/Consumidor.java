package br.edu.ufcg.ic.akka.java;

import java.util.Timer;
import java.util.TimerTask;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import akka.util.Timeout;
import br.edu.ufcg.ic.akka.java.Buffer.Empty;
import br.edu.ufcg.ic.akka.java.Buffer.Full;
import br.edu.ufcg.ic.akka.java.Buffer.Input;
import br.edu.ufcg.ic.akka.java.Produtor.Pausar;

public class Consumidor extends UntypedActor {
	private LoggingAdapter log;
	private static ActorRef buffer;
	private boolean pausado;
	private long espera;
	Timer temporizador = new Timer();
	TimerTask task = new TimerTask(){

		@Override
		public void run() {
			if(!pausado){
				buffer.tell(new Buffer.Output(), getSelf());
			}
		}
		
	};

	static public class Consumir {    
        public Consumir() {}
    }

	static public class Pausar {    
        public Pausar() {}
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
    	pausado = false;
    	espera = 0;
    }
	
	/*private void consumir(){
		while(consumir){
			try{
				Thread.sleep(espera);
			} catch (InterruptedException e){
				log.info(e.getMessage());
			}			
			buffer.tell(new Buffer.Output(), getSelf());
		}
	}*/
	
	public void onReceive(Object message) throws Exception {
        if (message instanceof Consumir){
        	if(!pausado){
        		startConsummation();
        	}
        }
        else if (message instanceof Empty) {
        	System.out.println("Buffer vazio.");
        } 
        else if (message instanceof Consumidor.Pausar) {
        	if(pausado){
				pausado = false;
				System.out.println("O consumidor foi resumido...");
			}else{
				pausado = true;
				System.out.println("O consumidor foi pausado...");
			}
        }
		else if(message instanceof Buffer.Input){
        	System.out.println("Consumidor recebeu int. input recebido: " + ((Buffer.Input)message).getNumero());
		} 
		else if (message instanceof TempoEspera) {
			espera = ((TempoEspera)message).getTempo();
		} else 
			unhandled(message);
    }
	
	private void startConsummation() {	
		try {
			temporizador.scheduleAtFixedRate(task, 10, espera);
		} catch (IllegalStateException e) {
		}
	}
}