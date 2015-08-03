package uk.camsw.rx.test.dsl.one;

import rx.functions.Action1;

public interface Resource1<T1, U, S extends AutoCloseable> {

    Resource1<T1, U, S> does(Action1<S> action);

    When1<T1, U> and();
}
