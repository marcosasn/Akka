package br.edu.ufcg.ic.akka.channel;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class TestChanSync {

	public static void main(String[] args) throws InterruptedException {
        ActorSystem system = ActorSystem.create("MySystem");
        //cria channels, readers and writers
        ActorRef channel = system.actorOf(Props.create(Channel.class), "channel");
        ActorRef reader = system.actorOf(Props.create(ChannelReader.class,channel), "reader");
        ActorRef reader2 = system.actorOf(Props.create(ChannelReader.class,channel), "reader2");
        ActorRef reader3 = system.actorOf(Props.create(ChannelReader.class,channel), "reader3");
        ActorRef writer = system.actorOf(Props.create(ChannelWriter.class,channel), "writer");
        
        reader.tell(new ChannelReader.StartReader(), ActorRef.noSender()); //inicia um reader
        reader2.tell(new ChannelReader.StartReader(), ActorRef.noSender());
        reader3.tell(new ChannelReader.StartReader(), ActorRef.noSender());
        
        //espera um pouco para todos os atores iniciarem
        Thread.sleep(1000);
        
        System.out.println("iniciando o writer");
        //inicia o writer e ele comunica o output no canal que vai destravar os leitores
        writer.tell(new ChannelWriter.StartWrite(), ActorRef.noSender());
        
        system.terminate();
	}
}
