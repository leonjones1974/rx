package uk.camsw.rxtest.dsl.one;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.functions.Func2;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

public interface Given1<T1, U> {

    Given1<T1, U> createSubject(Func1<Observable<T1>, Observable<U>> f);

    Given1<T1, U> createSubjectWithScheduler(Func2<Observable<T1>, Scheduler, Observable<U>> f);

    When1<T1, U> when();

    Given1<T1, U> errorsAreHandled();

    Given1<T1, U> renderer(Func1<U, String> renderer);

    Given1<T1, U> asyncTimeout(long timeout, TemporalUnit unit);

    Given1<T1, U> asyncTimeout(Duration duration);
}
