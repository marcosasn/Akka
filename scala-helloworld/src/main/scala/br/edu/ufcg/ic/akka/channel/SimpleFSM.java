package br.edu.ufcg.ic.akka.channel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import akka.actor.AbstractFSM;
import akka.actor.ActorRef;
import scala.concurrent.duration.Duration;

final class SetTarget {
	private final ActorRef ref;

	public SetTarget(ActorRef ref) {
		this.ref = ref;
	}

	public ActorRef getRef() {
		return ref;
	}
	// boilerplate ...
}

final class Queue {
	private final Object obj;

	public Queue(Object obj) {
		this.obj = obj;
	}

	public Object getObj() {
		return obj;
	}
	// boilerplate ...
}

final class Batch {
	private final List<Object> list;

	public Batch(List<Object> list) {
		this.list = list;
	}

	public List<Object> getList() {
		return list;
	}
	// boilerplate ...
}

enum Flush {
	Flush
}

enum Stat {
	Idle, Active
}

// state data
interface Data {
}

enum Uninitialized implements Data {
	Uninitialized
}

final class Todo implements Data {
	private final ActorRef target;
	private final List<Object> queue;

	public Todo(ActorRef target, List<Object> queue) {
		this.target = target;
		this.queue = queue;
	}

	public ActorRef getTarget() {
		return target;
	}

	public List<Object> getQueue() {
		return queue;
	}

	public Data copy(LinkedList linkedList) {
		return null;
	}
}

public class SimpleFSM extends AbstractFSM<Stat, Data> {
	{
		startWith(Stat.Idle, Uninitialized.Uninitialized);

		when(Stat.Idle, matchEvent(SetTarget.class, Uninitialized.class,
				(setTarget, uninitialized) -> stay().using(new Todo(setTarget.getRef(), new LinkedList<>()))));

		// transition elided ...

		when(Stat.Active, Duration.create(1, "second"), matchEvent(Arrays.asList(Flush.class, StateTimeout()), Todo.class,
				(event, todo) -> goTo(Stat.Idle).using(todo.copy(new LinkedList<>()))));

		// unhandled elided ...

		initialize();
	}
}
