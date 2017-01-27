package org.gaurav.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.Futures;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import scala.concurrent.Future;

/**
 * Created by Gaurav on 14-01-2017.
 */
public class ChildActor1 extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    ActorRef workerActorRef;
    public ChildActor1() {
        workerActorRef = getContext().actorOf(Props.create(GrandChildActor.class),
                "grandChildActor");
    }



    public void onReceive(Object o) throws Exception {
        log.info("Inside child actor1 " + getSelf());
        Future<Object> future =  Patterns.ask(workerActorRef, "To My GrandChild", 100000);
        Patterns.pipe(future, getContext().dispatcher()).pipeTo(getSender(), getSelf());
    }


}
