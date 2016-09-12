package br.edu.ufcg.ic.akka.java;

import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Identify;
import akka.actor.Terminated;
import akka.actor.UntypedActor;

public class Follower extends UntypedActor {
    final String identifyId = "1";
    {
        ActorSelection selection = getContext().actorSelection("/user/another");
        selection.tell(new Identify(identifyId), getSelf());
    }
    ActorRef another;
    final ActorRef probe;

    public Follower(ActorRef probe) {
        this.probe = probe;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof ActorIdentity) {
            ActorIdentity identity = (ActorIdentity) message;
            if (identity.correlationId().equals(identifyId)) {
                ActorRef ref = identity.getRef();
                if (ref == null)
                    getContext().stop(getSelf());
                else {
                    another = ref;
                    getContext().watch(another);
                    probe.tell(ref, getSelf());
                }
        	}
        } else if (message instanceof Terminated) {
            final Terminated t = (Terminated) message;
            if (t.getActor().equals(another)) {
                getContext().stop(getSelf());
            }
	    } else {
	        unhandled(message);
	    }
    }
}
