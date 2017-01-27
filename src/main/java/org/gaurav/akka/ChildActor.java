package org.gaurav.akka;

import akka.actor.UntypedActor;
import akka.dispatch.Futures;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import kamon.annotation.Count;
import kamon.annotation.EnableKamon;

/**
 * Created by Gaurav on 14-01-2017.
 */
@EnableKamon
public class ChildActor extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Count(name="ChildActor")
    public void onReceive(Object o) throws Exception {
        log.info("Inside child actor " + getSelf());
        if (o instanceof Number) {
            Patterns.pipe(Futures.successful("Successful"), getContext().dispatcher()).pipeTo(getSender(), getSelf());
        }
        else {
            throw new ArithmeticException();
        }

    }


}
