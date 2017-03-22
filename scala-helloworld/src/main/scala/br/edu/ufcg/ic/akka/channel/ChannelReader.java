package br.edu.ufcg.ic.akka.channel;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.Creator;
import akka.pattern.Patterns;
import akka.util.Timeout;
import br.edu.ufcg.ic.akka.channel.Channel.OutputEvent;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class ChannelReader extends BaseOut {

	static public class StartReader {	}

	private int numero;
	private static ActorRef channel;

	public ChannelReader(ActorRef channel) {
		super();
		ChannelReader.channel = channel;
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

	public void readFromChannel(ActorRef channel) {
		if (channel != null) {
			channel.tell(new Channel.OutputEvent(2), getSelf());
		}
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if (getState() == State.state_output) {
			if (message instanceof OutputEvent) {
				// alguem escreveu no canal
				int valor = ((OutputEvent) message).getValor();
				System.out.println("Leitor " + getSelf().path().name() + " leu valor " + valor);
				transition(getState(), message);
			} else if (message instanceof StartReader) {
				syso("reader: sending read channel request");
				transition(getState(), message);
				//Timeout timeout = new Timeout(Duration.create(5, "seconds"));
				//Future<Object> future = Patterns.ask(channel, new Channel.OutputEvent(2), timeout);
				//OutputEvent result = (OutputEvent) Await.result(future, timeout.duration());
				readFromChannel(channel);
				//System.out.println("reader recebeu valor " + result.getValor());
			} else {
				syso((String)message);
			}
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
		ChannelReader.channel = channel;
	}

	@Override
	protected void transition(State old, Object event) {
		if (old == State.state_output && event instanceof StartReader) {
			//syso("output... state: " + getState());
			setState(State.state_output);
		} else if (old == State.state_output && event instanceof OutputEvent) {
			//syso("output... state: " + getState());
			setState(State.state_output);
		}
	}
}
