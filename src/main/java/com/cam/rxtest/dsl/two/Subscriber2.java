package com.cam.rxtest.dsl.two;

public interface Subscriber2<T1, T2, U> {

    When2<T1, T2, U> subscribes();

    When2<T1, T2, U> unsubscribes();
}