package br.edu.ufcg.ic.akka.eventbus;

public class TypedEvent<S> implements Event {
	
	private S value;
	
	public TypedEvent(S value){
		this.value = value;
	}
	
	public S getValor() {
		return value;
	}

}
