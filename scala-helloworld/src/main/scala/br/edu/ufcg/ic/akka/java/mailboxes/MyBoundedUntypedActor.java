package br.edu.ufcg.ic.akka.java.mailboxes;

import akka.dispatch.RequiresMessageQueue;
import akka.dispatch.BoundedMessageQueueSemantics;
import br.edu.ufcg.ic.akka.java.MyUntypedActor;

public class MyBoundedUntypedActor extends MyUntypedActor implements RequiresMessageQueue<BoundedMessageQueueSemantics>{

}
