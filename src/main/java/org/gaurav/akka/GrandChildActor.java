package org.gaurav.akka;

import akka.actor.UntypedActor;
import akka.dispatch.Futures;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;

/**
 * Created by Gaurav on 15-01-2017.
 */
public class GrandChildActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public void onReceive(Object o) throws Exception {
        log.info("Inside Grandchild actor1 " + getSelf());
        Patterns.pipe(Futures.successful("Child1"), getContext().dispatcher()).pipeTo(getSender(), getSelf());
    }

}
