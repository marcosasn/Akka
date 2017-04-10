package br.edu.ufcg.ic.akka.eventbus;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.Creator;
import br.edu.ufcg.ic.akka.eventbus.Reader.Start;
import br.edu.ufcg.ic.akka.eventbus.Channel.Input;

public class Writer extends Base{
	
	private static ActorSystem system;
	
	public Writer(ActorSystem system) {
		super();
		Writer.system = system;
	}

	public static Props props() {
        return Props.create(new Creator<Writer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Writer create() throws Exception {
                return new Writer(system);
            }

        });
    }

	@Override
	public void onReceive(Object message) throws Throwable {
		if(getState() == State.state_input){
			if(message instanceof Start){
				if(system != null){
					system.eventStream().publish(new Input(2), getSelf());
				}
				
				transition(getState(), message);
			} else {
				syso(message.toString());
			}
		}
	}

	@Override
	protected void transition(State old, Object event) {
		if (old == State.state_input && event instanceof Start) {
			setState(State.state_input);
		}
	}
}