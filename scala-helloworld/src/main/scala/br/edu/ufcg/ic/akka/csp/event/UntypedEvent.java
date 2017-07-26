package br.edu.ufcg.ic.akka.csp.event;

public class UntypedEvent implements Event {
	
	private String msg;
	
	public UntypedEvent(String msg){
		this.msg = msg;
	}
	
	public String getMessage() {
		return msg;
	}

}
