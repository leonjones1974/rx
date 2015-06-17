package com.cam.rxtest;

import org.assertj.core.api.AbstractThrowableAssert;
import rx.subscriptions.SerialSubscription;

import static org.assertj.core.api.Assertions.assertThat;

public class Subscriber<T, U> {

    private final rx.observers.TestSubscriber<U> inner;
    private final String id;
    private final ExecutionContext<T, U> context;
    private final SerialSubscription subscription = new SerialSubscription();

    public Subscriber(String id, ExecutionContext<T, U> context) {
        this.id = id;
        this.inner = new rx.observers.TestSubscriber<>();
        this.context = context;
    }

    public String getId() {
        return id;
    }

    public When<T, U> subscribes() {
        context.addCommand(c -> subscription.set(c.getStreamUnderTest().subscribe(inner)));
        return new When<>(context);
    }

    public When<T, U> unsubscribes() {
        context.addCommand(c -> subscription.unsubscribe());
        return new When<>(context);
    }

    public int eventCount() {
        return inner.getOnNextEvents().size();
    }

    public U event(int index) {
        return getEvent(index);
    }

    private U getEvent(int index) {
        return inner.getOnNextEvents().get(index);
    }

    public int completedCount() {
        return inner.getOnCompletedEvents().size();
    }

    public Class<? extends Throwable> errorClass() {
        //noinspection ThrowableResultOfMethodCallIgnored
        return inner.getOnErrorEvents().get(0).getClass();
    }

    public String errorMessage() {
        return inner.getOnErrorEvents().get(0).getMessage();
    }

    public AbstractThrowableAssert<?, ? extends Throwable> error(int index) {
        return assertThat(inner.getOnErrorEvents().get(index));
    }

}
