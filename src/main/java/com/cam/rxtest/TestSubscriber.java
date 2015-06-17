package com.cam.rxtest;

import org.assertj.core.api.*;
import rx.Observable;
import rx.Observer;
import rx.subscriptions.SerialSubscription;

import static org.assertj.core.api.Assertions.assertThat;

public class TestSubscriber<T> implements Observer<T> {

    private final rx.observers.TestSubscriber<T> inner;
    private SerialSubscription subscription = new SerialSubscription();

    public TestSubscriber(String id) {
        this.inner = new rx.observers.TestSubscriber<T>();
    }

    @Override
    public void onCompleted() {
        inner.onCompleted();
    }

    @Override
    public void onError(Throwable e) {
        inner.onError(e);
    }

    @Override
    public void onNext(T t) {
        inner.onNext(t);
    }

    public AbstractIntegerAssert<?> eventCount() {
        return assertThat(inner.getOnNextEvents().size());
    }

    public void subscribeTo(Observable<T> subject) {
        subscription.set(subject.subscribe(this));
    }

    public AbstractObjectAssert<?, T> event(int index) {
        return assertThat(getEvent(index));
    }

    private T getEvent(int index) {
        return inner.getOnNextEvents().get(index);
    }

    public AbstractIntegerAssert<?> completedCount() {
        return assertThat(inner.getOnCompletedEvents().size());
    }

    public AbstractIntegerAssert<?> errorCount() {
        return assertThat(inner.getOnErrorEvents().size());
    }

    public AbstractClassAssert<?> errorClass(int index) {
        return assertThat(inner.getOnErrorEvents().get(index).getClass());
    }

    public AbstractCharSequenceAssert<?, String> errorMessage(int index) {
        return assertThat(inner.getOnErrorEvents().get(index).getMessage());
    }

    public AbstractThrowableAssert<?, ? extends Throwable> error(int index) {
        return assertThat(inner.getOnErrorEvents().get(index));
    }

    public void unsubscribe() {
        subscription.unsubscribe();
    }
}
