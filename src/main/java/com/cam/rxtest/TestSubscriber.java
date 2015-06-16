package com.cam.rxtest;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import rx.Observer;

import java.sql.Timestamp;
import java.util.function.Function;

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
        return Assertions.assertThat(1);
    }

    public T event(int index) {
        return (T) "subscriber1";
    }

    public AbstractObjectAssert<?, Object> eventValue(int index, Function<T, Object> f) {
        Object value = f.apply(event(index));
        return Assertions.assertThat(value);
    }
}
