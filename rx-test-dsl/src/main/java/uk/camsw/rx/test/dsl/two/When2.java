package uk.camsw.rx.test.dsl.two;

import uk.camsw.rx.test.dsl.impl.Then;

public interface When2<T1, T2, U> {

    Subscriber2<T1, T2, U> subscriber(String id);

    Source2<T1, T1, T2, U> source1();

    Source2<T2, T1, T2, U> source2();

    Then<U> then();

    Time2<T1, T2, U> time();

    <S extends AutoCloseable> Resource2<T1, T2, U, S> resource(String id);
}
