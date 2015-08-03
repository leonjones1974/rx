package uk.camsw.rx.test.dsl.two;

import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

public interface Given2<T1, T2, U> {

    Given2<T1, T2, U> createSubject(Func2<Observable<T1>, Observable<T2>, Observable<U>> f);

    When2<T1, T2, U> when();

    Given2<T1, T2, U> errorsAreHandled();

    Given2<T1, T2, U> renderer(Func1<U, String> renderer);

    Given2<T1, T2, U> asyncTimeout(long timeout, TemporalUnit unit);

    Given2<T1, T2, U> asyncTimeout(Duration duration);

    Given2<T1, T2, U> theResource(Func0<? extends AutoCloseable> f);

}
