package br.edu.ufcg.ic.akka.java.faulttolerance;

import java.util.Timer;
import java.util.TimerTask;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import br.edu.ufcg.ic.akka.java.Buffer.Empty;
import br.edu.ufcg.ic.akka.java.faulttolerance.Buffer.BufferApi.Input;
import br.edu.ufcg.ic.akka.java.faulttolerance.Buffer.BufferApi.Output;
import br.edu.ufcg.ic.akka.java.faulttolerance.Consumidor.ConsumidorApi.Consumir;
import br.edu.ufcg.ic.akka.java.faulttolerance.Produtor.ProdutorApi.Pausar;
import br.edu.ufcg.ic.akka.java.faulttolerance.Consumidor.ConsumidorApi.TempoEspera;
import br.edu.ufcg.ic.akka.java.faulttolerance.Produtor.ProdutorApi.UseBuffer;

public class Consumidor extends UntypedActor {
	
	public interface ConsumidorApi {
		
		public static class Consumir {    
	        public Consumir() {}
	    }
		
		public static class TempoEspera {
	        private final int tempo;
	        
	        public TempoEspera(int tempo) {
	            this.tempo = tempo;
	        }

	        public int getTempo() {
	    		return tempo;
	        }
	    }		
	}

	private LoggingAdapter log;
	private static ActorRef buffer;
	private boolean pausado;
	private long espera;
	Timer temporizador = new Timer();
	TimerTask task = new TimerTask(){

		@Override
		public void run() {
			if(!pausado && buffer != null){
				buffer.tell(new Output(), getSelf());
			}
		}		
	};
	
	public static Props props() {
        return Props.create(new Creator<Consumidor>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Consumidor create() throws Exception {
                return new Consumidor();
            }

        });
    }
	
	public Consumidor() {
    	log = Logging.getLogger(getContext().system(), this);
    	pausado = false;
    	espera = 0;
    }
	
	public void onReceive(Object message) throws Exception {
		if (message instanceof UseBuffer){
        	buffer = ((UseBuffer)message).buffer;
        } else if (message instanceof Consumir){
        	if(!pausado && buffer != null){
        		startConsummation();
        	}
        }
        else if (message instanceof Empty) {
        	System.out.println("Buffer vazio.");
        } 
        else if (message instanceof Pausar) {
        	if(pausado){
				pausado = false;
				System.out.println("O consumidor foi resumido...");
			}else{
				pausado = true;
				System.out.println("O consumidor foi pausado...");
			}
        }
		else if(message instanceof Input){
        	System.out.println("Consumidor recebeu int. input recebido: " + ((Input)message).getNumero());
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