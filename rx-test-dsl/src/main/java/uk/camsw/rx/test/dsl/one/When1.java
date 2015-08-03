package uk.camsw.rx.test.dsl.one;

import uk.camsw.rx.test.dsl.impl.Then;

public interface When1<T1, U> {

    Source1<T1, T1, U> theSource();

    Subscriber1<T1, U> subscriber(String id);

    Then<U> then();

    void go();

    Time1<T1, U> time();

    <S extends AutoCloseable> Resource1<T1, U, S> resource(String id);
}
