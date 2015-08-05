package uk.camsw.rx.test.dsl.base;

import uk.camsw.rx.test.dsl.impl.Then;

public interface IWhen<T1, U, TIME extends ITime> {

    Then<U> then();

    void go();

    TIME time();

//    <S extends AutoCloseable> Resource1<T1, U, S> resource(String id);
}
