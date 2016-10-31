package br.edu.ufcg.ic.akka.java.faulttolerance;

import java.util.Timer;
import java.util.TimerTask;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import br.edu.ufcg.ic.akka.java.faulttolerance.Buffer.BufferApi.Full;
import br.edu.ufcg.ic.akka.java.faulttolerance.Buffer.BufferApi.Input;
import br.edu.ufcg.ic.akka.java.faulttolerance.Consumidor.ConsumidorApi.TempoEspera;
import br.edu.ufcg.ic.akka.java.faulttolerance.Produtor.ProdutorApi.Pausar;
import br.edu.ufcg.ic.akka.java.faulttolerance.Produtor.ProdutorApi.Produzir;
import br.edu.ufcg.ic.akka.java.faulttolerance.Produtor.ProdutorApi.UseBuffer;

public class Produtor extends UntypedActor{
	
	public interface ProdutorApi{	
		public static class Produzir {    
	        public Produzir() {}
	    }
		
		public static class Pausar {    
	        public Pausar() {}
	    }
		
		public static class UseBuffer {
			public final ActorRef buffer;

			public UseBuffer(ActorRef buffer) {
				this.buffer = buffer;
			}
		}
	}
	
	private LoggingAdapter log;
	private static ActorRef buffer;
	private boolean pausado;
	private int produto;
	private long espera;
	Timer temporizador = new Timer();
	TimerTask task = new TimerTask(){

		@Override
		public void run() {
			if(!pausado && buffer != null){
				buffer.tell(new Input(produto), getSelf());
				produto++;
			}
		}
		
	};
	
	public static Props props() {
        return Props.create(new Creator<Produtor>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Produtor create() throws Exception {
                return new Produtor();
            }

        });
    }
	
	public Produtor() {
		log = Logging.getLogger(getContext().system(), this);
    	pausado = false;
    	produto = 0;
    	espera = 0;
    }
	
    public void onReceive(Object message) throws Exception {
    	if (message instanceof UseBuffer){
        	buffer = ((UseBuffer)message).buffer;
        } else if (message instanceof Produzir){
        	if(!pausado && buffer != null){
        		startProduction();
        	}
        }
        else if (message instanceof Full) {
        	System.out.println("Buffer cheio. input perdido: " + ((Full) message).getInput());
            
        }  
        else if (message instanceof Pausar) {
			if(pausado){
				pausado = false;
				System.out.println("O produtor foi resumido...");
			}else{
				pausado = true;
				System.out.println("O produtor foi pausado...");
			}
        }
        else if (message instanceof TempoEspera) {
			espera = ((TempoEspera)message).getTempo();
		}
		else 
			unhandled(message);
    }

	private void startProduction() {		
		try {
			temporizador.scheduleAtFixedRate(task, 10, espera);
		} catch (IllegalStateException e) {
		}
	}
}