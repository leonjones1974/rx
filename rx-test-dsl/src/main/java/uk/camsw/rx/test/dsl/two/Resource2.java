package uk.camsw.rx.test.dsl.two;

import rx.functions.Action1;

public interface Resource2<T1, T2, U, S extends AutoCloseable> {

    Resource2<T1, T2, U, S> does(Action1<S> action);

    When2<T1, T2, U> and();
}
