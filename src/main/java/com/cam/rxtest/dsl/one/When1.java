package com.cam.rxtest.dsl.one;

import com.cam.rxtest.dsl.impl.Then;

public interface When1<T1, U> {

    Source1<T1, T1, U> theSource();

    Subscriber1<T1, U> subscriber(String id);

    Then<U> then();

    Time1<T1, U> time();
}
