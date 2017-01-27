package org.gaurav.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import kamon.Kamon;
import kamon.annotation.Histogram;
import kamon.metric.instrument.Counter;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;


/**
 * Hello world!
 */

public class App {

     public static void main(String[] args) {

        try {
            Kamon.start();
            final kamon.metric.instrument.Histogram someHistogram = Kamon.metrics().histogram("some-histogram");
            final Counter someCounter = Kamon.metrics().counter("some-counter");

            someHistogram.record(42);
            someHistogram.record(50);
            someCounter.increment();

            ActorSystem system = ActorSystem.create("faultTolerance");
            ActorRef supervisor = system.actorOf(Props.create(ParentActor.class),
                    "supervisor");
            try {
                String result = (String) Await.result(
                        Patterns.ask(supervisor, "Handa", 5000),
                        Duration.create(50000, TimeUnit.MILLISECONDS));
            }catch(Exception exception){
                System.out.println("First Request failed");
                //exception.printStackTrace();
            }

            String result1 = (String) Await.result(
                    Patterns.ask(supervisor, 10000, 5000),
                    Duration.create(50000, TimeUnit.MILLISECONDS));


            System.out.println(result1);

        } catch (Exception ex) {
            System.out.println("Main exception occurred");
            //ex.printStackTrace();
        }
        finally {

        }
    }
}
