package uk.camsw.rxtest.dsl.one;

import uk.camsw.rxtest.dsl.impl.Then;

public interface When1<T1, U> {

    Source1<T1, T1, U> theSource();

    Subscriber1<T1, U> subscriber(String id);

    Then<U> then();

    void go();

    Time1<T1, U> time();
}
