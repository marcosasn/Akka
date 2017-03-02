package br.edu.ufcg.ic.akka.channel;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;

public class ChannelWriter extends UntypedActor{

	private static ActorRef channel;
	
	public ChannelWriter(ActorRef channel) {
		super();
		this.channel = channel;
	}

	public static Props props() {
        return Props.create(new Creator<ChannelWriter>() {
            private static final long serialVersionUID = 1L;

            @Override
            public ChannelWriter create() throws Exception {
                return new ChannelWriter(channel);
            }

        });
    }
	
	static public class StartWrite{
		
	}
	public void writeOnChannel(ActorRef channel){
		if(channel != null){
			channel.tell(new Channel.InputEvent(2), getSelf());
		}
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if(message instanceof StartWrite){
			writeOnChannel(channel);
		}	
	}

	public ActorRef getChannel() {
		return channel;
	}

	public void setChannel(ActorRef channel) {
		this.channel = channel;
	}
	
}
