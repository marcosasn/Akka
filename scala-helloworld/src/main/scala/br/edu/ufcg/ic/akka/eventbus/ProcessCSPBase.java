package br.edu.ufcg.ic.akka.eventbus;

import java.util.LinkedList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.japi.Procedure;
import br.edu.ufcg.ic.akka.eventbus.ProcessCSP.ProcessCSPApi.InterState;;

public abstract class ProcessCSPBase extends UntypedActor {
	protected static enum State {
		started, deadlock, sucess;
	}
	
	ActorRef requesterSkipRef;
	private static LinkedList<String> initials;
	private State state;
	Procedure<Object> nextBehavior;
	
	Procedure<Object> skip = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
        	if(getState() == State.sucess){
        		requesterSkipRef.tell("Sucess", getSelf());
        		setState(State.deadlock);
        		getContext().become(deadlock);
        	}
        }
    };
	
    Procedure<Object> deadlock = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
        	if(getState() == State.deadlock){
        		syso(getSelf().path().name().toString() + " is deadlock");
        		getSender().tell(new InterState(getState()), getSelf());
        	}
        }
    };
	
	protected void initialize(List<String> initials) {
		state = State.started;
		listToLinkedList(initials);
	}

	protected void setState(State s) {
		if (state != s) {
			state = s;
		}
	}

	protected State getState() {
		return state;
	}
	
	protected static LinkedList<String> getInitials() {
		return initials;
	}
	
	protected void listToLinkedList(List<String> initials){
		ProcessCSPBase.initials = new LinkedList<String>();
		for(String s: initials){
			ProcessCSPBase.initials.add(s);
		}
	}
	
	protected void updateInitials(){
		initials.removeFirst();
	}
	
	protected boolean isCurrenteEvent(String event){
		return getInitials().getFirst().equals(event);
	}

	protected void syso(String msg){
		System.out.println(msg);
	}
		
	protected void peform(String event){
		getSelf().tell(event, getSelf());
	}	
	
	abstract protected void transition(State old, String event);	
}