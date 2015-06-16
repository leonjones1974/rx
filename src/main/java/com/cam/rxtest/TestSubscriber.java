package com.cam.rxtest;

import org.assertj.core.api.AbstractIntegerAssert;
import rx.Observer;

import java.sql.Timestamp;

public class TestSubscriber<T> implements Observer<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(T t) {

    }

    public AbstractIntegerAssert<?> eventCount() {
        return org.assertj.core.api.Assertions.assertThat(1);
    }

    public T event(int index) {
        return (T) "subscriber1";
    }
}
