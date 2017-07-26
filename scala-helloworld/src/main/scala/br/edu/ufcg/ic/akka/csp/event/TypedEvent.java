package br.edu.ufcg.ic.akka.csp.event;

public class TypedEvent<S> implements Event {
	
	private S msg;
	
	public TypedEvent(S msg){
		this.msg = msg;
	}
	
	public S getMessage() {
		return msg;
	}

}
