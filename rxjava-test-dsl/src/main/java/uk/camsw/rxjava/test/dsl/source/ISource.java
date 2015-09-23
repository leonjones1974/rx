package uk.camsw.rxjava.test.dsl.source;

import rx.Observable;
import uk.camsw.rxjava.test.dsl.when.IWhen;

public interface ISource<T, WHEN extends IWhen> {

    WHEN emits(T event);

    WHEN completes();

    WHEN  errors(Throwable t);

    Observable<T> asObservable();

}
