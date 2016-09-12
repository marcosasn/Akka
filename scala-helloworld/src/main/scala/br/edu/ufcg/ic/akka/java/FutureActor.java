package br.edu.ufcg.ic.akka.java;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import akka.util.Timeout;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class FutureActor extends UntypedActor {
	
	static public class Result {
        private final String x;
        private final String s;
        
        public Result(String x, String s) {
            this.x = x;
            this.s = s;
        }
    }
	
	public static Props props() {
        return Props.create(new Creator<FutureActor>() {
            private static final long serialVersionUID = 1L;

            @Override
            public FutureActor create() throws Exception {
                return new FutureActor();
            }

        });
    }
	
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void onReceive(Object message) throws Throwable {
		if (message instanceof String) {
            log.info("Received String message: {}", message);
            getSender().tell(message, getSelf());
        } else{
        	unhandled(message);
        }
	}
	
	public static void main(String args[]) {
	
		final ActorSystem system = ActorSystem.create("MySystem");
		final ActorRef actorA = system.actorOf(Props.create(MyUntypedActor.class),"actorA");
		final ActorRef actorB = system.actorOf(Props.create(MyUntypedActor.class),"actorB");
		final ActorRef actorC = system.actorOf(Props.create(MyUntypedActor.class),"actorC");
		
		final Timeout t = new Timeout(Duration.create(5, TimeUnit.SECONDS));

		final ArrayList<Future<Object>> futures = new ArrayList<Future<Object>>();
		futures.add(ask(actorA, "request", 1000)); // using 1000ms timeout
		futures.add(ask(actorB, "another request", 1000)); // using timeout from
		// above
	
		final Future<Iterable<Object>> aggregate = Futures.sequence(futures,
		    system.dispatcher());
	
		final Future<Result> transformed = aggregate.map(
		    new Mapper<Iterable<Object>, Result>() {
		        public Result apply(Iterable<Object> coll) {
		            final Iterator<Object> it = coll.iterator();
		            final String x = (String) it.next();
		            final String s = (String) it.next();
		            return new Result(x,s);
		        }
		    }, system.dispatcher());//novo futuro
		pipe(transformed, system.dispatcher()).to(actorC);
		
		/*system.stop(actorA);
    	system.stop(actorB);
    	system.stop(actorC);
    	system.shutdown();*/
	}
}
