package com.cam.rxtest;

import org.assertj.core.api.AbstractThrowableAssert;

public interface Subscriber1<T1, U> {

    String getId();

    When1<T1, U> subscribes();

    When1<T1, U> unsubscribes();

    int eventCount();

    U event(int index);

    int completedCount();

    Class<? extends Throwable> errorClass();

    String errorMessage();

    AbstractThrowableAssert<?, ? extends Throwable> error(int index);
}
