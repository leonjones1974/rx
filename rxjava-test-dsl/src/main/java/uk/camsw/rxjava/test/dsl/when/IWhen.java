package uk.camsw.rxjava.test.dsl.when;

import rx.functions.Action0;
import uk.camsw.rxjava.test.dsl.subscriber.ISubscriber;
import uk.camsw.rxjava.test.dsl.then.IThen;
import uk.camsw.rxjava.test.dsl.time.ITime;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

public interface IWhen<U, WHEN extends IWhen> {

    ISubscriber<U, WHEN> subscriber(int id);

//    ISubscriber<U, WHEN> subscriber(String id);

    ISubscriber<U, WHEN> theSubscriber(String id);      // Alias

    WHEN theCurrentThreadSleepsFor(long time, TemporalUnit unit);

    WHEN sleepFor(long time, TemporalUnit unit);       // Alias

    WHEN theCurrentThreadSleepsFor(Duration duration);

    WHEN sleepFor(Duration duration);                   // Alias

    ISubscriber<U, WHEN> theSubscriber();

    ITime<WHEN> time();

    WHEN actionIsPerformed(Action0 action);

    WHEN theActionIsPerformed(Action0 action);

    IThen<U> then();

    void go();

}
