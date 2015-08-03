package uk.camsw.rx.test.dsl.one;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

public interface Given1<T1, U> {

    Given1<T1, U> subjectCreated(Func1<Observable<T1>, Observable<U>> f);

    Given1<T1, U> subjectCreatedWithScheduler(Func2<Observable<T1>, Scheduler, Observable<U>> f);

    When1<T1, U> when();

    Given1<T1, U> errorsAreHandled();

    Given1<T1, U> renderer(Func1<U, String> renderer);

    Given1<T1, U> asyncTimeout(long timeout, TemporalUnit unit);

    Given1<T1, U> asyncTimeout(Duration duration);

    Given1<T1, U> theResource(String id, Func0<? extends AutoCloseable> f);

    Given1<T1, U> theResource(Func0<? extends AutoCloseable> f);
}
