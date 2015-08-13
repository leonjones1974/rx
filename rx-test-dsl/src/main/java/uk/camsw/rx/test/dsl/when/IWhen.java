package uk.camsw.rx.test.dsl.when;

import uk.camsw.rx.test.dsl.given.IGiven;
import uk.camsw.rx.test.dsl.scenario.ExecutionContext;
import uk.camsw.rx.test.dsl.subscriber.ISubscriber;
import uk.camsw.rx.test.dsl.then.IThen;
import uk.camsw.rx.test.dsl.time.ITime;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.function.Consumer;

public interface IWhen<U, WHEN extends IWhen> {

    ISubscriber<U, WHEN> subscriber(int id);
    ISubscriber<U, WHEN> subscriber(String id);
    ISubscriber<U, WHEN> theSubscriber(String id);      // Alias

    WHEN theCurrentThreadSleepsFor(long time, TemporalUnit unit);
    WHEN theCurrentThreadSleepsFor(Duration duration);

    ISubscriber<U, WHEN> theSubscriber();

    ITime<WHEN> time();

    WHEN actionIsPerformed(Consumer<ExecutionContext<?, ?, U, ? extends IGiven, WHEN>> action);

    IThen<U> then();

    void go();

}
