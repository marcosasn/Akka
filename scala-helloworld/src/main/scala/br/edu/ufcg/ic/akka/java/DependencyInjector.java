package br.edu.ufcg.ic.akka.java;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import br.edu.ufcg.ic.akka.MyActor;

public class DependencyInjector implements IndirectActorProducer{
	final Object applicationContext;
    final String beanName;

    public DependencyInjector(Object applicationContext, String beanName) {
         this.applicationContext = applicationContext;
         this.beanName = beanName;
     }

    public Class<? extends Actor> actorClass() {
    	return MyActor.class;
    }

    public MyActor produce() {
    	MyActor result = null;
    	// obtain fresh Actor instance from DI framework ...
    	return result;
    }
    
    public static void main(String args[]) {
    	//final ActorRef myActor = getContext().actorOf(Props.create(DependencyInjector.class, applicationContext, "MyActor"),
    		//	"myactor3");

    }
}
