package br.edu.ufcg.ic.akka.java;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import br.edu.ufcg.ic.akka.java.Buffer.Full;
import br.edu.ufcg.ic.akka.java.Consumidor.TempoEspera;

public class Produtor extends UntypedActor{
	private LoggingAdapter log;
	private static ActorRef buffer;
	private boolean produzir;
	private int produto;
	private long espera;
	
	static public class Produzir {    
        public Produzir() {}
    }
	
	static public class Pausar {    
        public Pausar() {}
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
		log = Logging.getLogger(getContext().system(), this);
    	Produtor.buffer = buffer;
    	produzir = false;
    	produto = 0;
    	espera = 0;
    }

	
	public void produzir() throws InterruptedException{
		while(produzir){
			try{
				Thread.sleep(espera);
			} catch(InterruptedException e){
				log.info(e.getMessage());
			}
			buffer.tell(new Buffer.Input(produto), getSelf());
			System.out.println("Inteiro produzido " + produto);
			produto++;
		}
	}
	
    public void onReceive(Object message) throws Exception {
        if (message instanceof Produzir){
        	produzir = true;
        	while(produzir){
    			try{
    				Thread.sleep(espera);
    			} catch(InterruptedException e){
    				log.info(e.getMessage());
    			}
    			buffer.tell(new Buffer.Input(produto), getSelf());
    			System.out.println("Inteiro produzido " + produto);
    			produto++;
    		}
        }
        else if (message instanceof Full) {
			produzir = false;
			log.info("O buffer parece estar cheio...");
			System.out.println("O buffer parece estar cheio...");
        }
        else if (message instanceof Consumidor.TempoEspera) {
			espera = ((Consumidor.TempoEspera)message).getTempo();
		}  
        else if (message instanceof Pausar) {
			produzir = false;
			log.info("O produtor foi pausado...");
			System.out.println("O produtor foi pausado...");
        }
		else 
			unhandled(message);
    }

	public LoggingAdapter getLog() {
		return log;
	}

	public void setLog(LoggingAdapter log) {
		this.log = log;
	}

	public static ActorRef getBuffer() {
		return buffer;
	}

	public static void setBuffer(ActorRef buffer) {
		Produtor.buffer = buffer;
	}

	public boolean isProduzir() {
		return produzir;
	}

	public void setProduzir(boolean produzir) {
		this.produzir = produzir;
	}

	public int getProduto() {
		return produto;
	}

	public void setProduto(int produto) {
		this.produto = produto;
	}

	public long getEspera() {
		return espera;
	}

	public void setEspera(long espera) {
		this.espera = espera;
	}
    
    
}