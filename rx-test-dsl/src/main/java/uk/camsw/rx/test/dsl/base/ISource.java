package uk.camsw.rx.test.dsl.base;

import rx.Observable;

public interface ISource<T, WHEN extends IWhen> {

    WHEN emits(T event);

    WHEN completes();

    WHEN  errors(Throwable t);

    Observable<T> asObservable();

}
