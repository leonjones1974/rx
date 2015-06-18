package com.cam.rxtest;

import org.assertj.core.api.AbstractThrowableAssert;
import rx.subscriptions.SerialSubscription;

import static org.assertj.core.api.Assertions.assertThat;

public class Subscriber<T1, T2, U>
        implements Subscriber1<T1,U>,
        Subscriber2<T1, T2, U>
{

    private final rx.observers.TestSubscriber<U> inner;
    private final String id;
    private final ExecutionContext<T1, T2, U> context;
    private final SerialSubscription subscription = new SerialSubscription();

    public Subscriber(String id, ExecutionContext<T1, T2, U> context) {
        this.id = id;
        this.inner = new rx.observers.TestSubscriber<>();
        this.context = context;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public When<T1, T2, U> subscribes() {
        context.addCommand(c -> subscription.set(c.getStreamUnderTest().subscribe(inner)));
        return new When<>(context);
    }

    @Override
    public When<T1, T2, U> unsubscribes() {
        context.addCommand(c -> subscription.unsubscribe());
        return new When<>(context);
    }

    @Override
    public int eventCount() {
        return inner.getOnNextEvents().size();
    }

    @Override
    public U event(int index) {
        return getEvent(index);
    }

    private U getEvent(int index) {
        return inner.getOnNextEvents().get(index);
    }

    @Override
    public int completedCount() {
        return inner.getOnCompletedEvents().size();
    }

    @Override
    public Class<? extends Throwable> errorClass() {
        //noinspection ThrowableResultOfMethodCallIgnored
        return inner.getOnErrorEvents().get(0).getClass();
    }

    @Override
    public String errorMessage() {
        return inner.getOnErrorEvents().get(0).getMessage();
    }

    @Override
    public AbstractThrowableAssert<?, ? extends Throwable> error(int index) {
        return assertThat(inner.getOnErrorEvents().get(index));
    }

}
