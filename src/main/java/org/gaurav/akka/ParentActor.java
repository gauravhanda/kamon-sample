package org.gaurav.akka;

import akka.actor.*;
import akka.actor.SupervisorStrategy;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import akka.pattern.Patterns;
import kamon.annotation.Count;
import kamon.annotation.EnableKamon;
import kamon.annotation.Trace;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.Iterator;

import static akka.actor.SupervisorStrategy.*;

/**
 * Created by Gaurav on 14-01-2017.
 */
@EnableKamon
public class ParentActor extends UntypedActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);


    public ActorRef workerActor1;
    public ActorRef workerActor2;


    public ParentActor() {
        workerActor1 = getContext().actorOf(Props.create(ChildActor.class),
                "workerActor1");
        workerActor2 = getContext().actorOf(Props.create(ChildActor1.class),
                "workerActor2");

    }

    private static SupervisorStrategy strategy = new AllForOneStrategy(10,
            Duration.create("10 second"), new Function<Throwable, SupervisorStrategy.Directive>() {
        public SupervisorStrategy.Directive apply(Throwable t) {
            if (t instanceof ArithmeticException) {
                System.out.println("Inside ArithmeticException");
                return resume();
            } else if (t instanceof NullPointerException) {
                return restart();
            } else if (t instanceof IllegalArgumentException) {
                return stop();
            } else {
                return escalate();
            }
        }
    });

    //@Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Count(name="ParentActor")
    public void onReceive(Object msg) throws Exception {
        ArrayList<Future> obj = new ArrayList<Future>();
        obj.add(Patterns.ask(workerActor1, msg, 100000 ));
        obj.add(Patterns.ask(workerActor2, msg, 100000 ));
        Future<Iterable<Future>>  result = Futures.sequence((Iterable)obj, getContext().dispatcher());
        Future<String> finalFuture = result.flatMap(new Mapper<Iterable<Future>, Future<String>>() {
            @Override
            public Future<String> apply(Iterable<Future> parameter) {
                log.info("Inside the sequence handler....");
                return Futures.successful("Done with parallel processing");
            }
        }, getContext().dispatcher());

        Patterns.pipe(finalFuture, getContext().dispatcher()).pipeTo(getSender(), getSelf());

    }
}
