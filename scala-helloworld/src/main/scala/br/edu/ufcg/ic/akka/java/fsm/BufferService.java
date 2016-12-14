package br.edu.ufcg.ic.akka.java.fsm;

import static akka.actor.SupervisorStrategy.escalate;
import static akka.actor.SupervisorStrategy.restart;

import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.actor.SupervisorStrategy.Directive;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import akka.japi.Function;
import br.edu.ufcg.ic.akka.java.fsm.Buffer.BufferApi.BufferException;
import br.edu.ufcg.ic.akka.java.fsm.Buffer.BufferApi.GenerateBufferFailure;
import br.edu.ufcg.ic.akka.java.fsm.Produtor.ProdutorApi.Produzir;
import br.edu.ufcg.ic.akka.java.fsm.Produtor.ProdutorApi.UseBuffer;
import br.edu.ufcg.ic.swing.ListenerBuffer;
import br.edu.ufcg.ic.akka.java.fsm.Consumidor.ConsumidorApi.Consumir;
import scala.concurrent.duration.Duration;

public class BufferService extends UntypedActor {
	
	public static final Object Start = "Start";
	public static final Object Do = "Do";
	static final Object Reconnect = "Reconnect";
	final LoggingAdapter log;
	ActorRef buffer;
	static ActorRef consumidor;
	static ActorRef produtor;
	private static ListenerBuffer listener;		
	
	public static Props props() {
        return Props.create(new Creator<BufferService>() {
            private static final long serialVersionUID = 1L;

            @Override
            public BufferService create() throws Exception {
                return new BufferService(produtor, consumidor, listener);
            }

        });
    }
	
	public BufferService(ActorRef produtor, ActorRef consumidor, ListenerBuffer listenerBuffer ) {
    	log = Logging.getLogger(getContext().system(), this);
    	BufferService.consumidor = consumidor;
    	BufferService.produtor = produtor;
    	BufferService.listener = listenerBuffer;
    }

	private static SupervisorStrategy strategy = new OneForOneStrategy(3, Duration.create("5 seconds"),
			new Function<Throwable, Directive>() {
				@Override
				public Directive apply(Throwable t) {
					if (t instanceof BufferException) {
						return restart();
					} else {
						return escalate();
					}
				}
			});

	@Override
	public SupervisorStrategy supervisorStrategy() {
		System.out.println("supervisor strategy run...");
		return strategy;
	}

	void initBuffer() {
		System.out.println("initBuffer..");
		buffer = getContext().watch(getContext().actorOf(Props.create(Buffer.class, listener), "buffer"));

		if (produtor != null && consumidor != null)
			produtor.tell(new UseBuffer(buffer), getSelf());
			consumidor.tell(new UseBuffer(buffer), getSelf());
			produtor.tell(new Produzir(), getSelf());
			consumidor.tell(new Consumir(), getSelf());
	}
	
	void forward(Object msg) {
		buffer.forward(msg, getContext());
	}

	@Override
	public void onReceive(Object msg) {
		if(msg.equals(Start)){
			initBuffer();
		} else if (msg instanceof Terminated) {
			buffer = null;
			
			produtor.tell(new UseBuffer(buffer), getSelf());
			consumidor.tell(new UseBuffer(buffer), getSelf());
			getContext().system().scheduler().scheduleOnce(Duration.create(10, "seconds"), getSelf(), Reconnect,
					getContext().dispatcher(), null);
		} else if (msg.equals(Reconnect)) {
			System.out.println("reconecting buffer service...");
			initBuffer();
		} else if (msg instanceof GenerateBufferFailure){
			forward(msg);
		} else {
			unhandled(msg);
		}
	}
}