package br.edu.ufcg.ic.akka.channel;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import akka.pattern.Patterns;
import akka.util.Timeout;
import br.edu.ufcg.ic.akka.channel.Channel.OutputEvent;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class ChannelReader extends UntypedActor {

	private int numero;
	private static ActorRef channel;
	
	public ChannelReader(ActorRef channel) {
		super();
		this.channel = channel;
	}

	public static Props props() {
        return Props.create(new Creator<ChannelReader>() {
            private static final long serialVersionUID = 1L;

            @Override
            public ChannelReader create() throws Exception {
                return new ChannelReader(channel);
            }

        });
    }
	static public class StartReader{
		
	}

	public void readFromChannel(ActorRef channel){
		if(channel != null){
			
			channel.tell(new Channel.OutputEvent(2), getSelf());
		}
	}
	
	@Override
	public void onReceive(Object message) throws Throwable {
		if (message instanceof OutputEvent){
			//alguem escreveu no canal
			int valor = ((OutputEvent) message).getValor();
			System.out.println("Leitor " + numero + " leu valor " + valor);
		} else if (message instanceof StartReader){
			System.out.println("reader: sending read channel request");
			Timeout timeout = new Timeout(Duration.create(5, "seconds"));
			Future<Object> future = Patterns.ask(channel,new Channel.OutputEvent(2), timeout);
			OutputEvent result = (OutputEvent)Await.result(future, timeout.duration());
			System.out.println("reader recebeu valor " + result.getValor());
		}
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public ActorRef getChannel() {
		return channel;
	}

	public void setChannel(ActorRef channel) {
		this.channel = channel;
	}

	
}
