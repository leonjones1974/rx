package com.cam.rxtest.dsl.impl;

import com.cam.rxtest.dsl.assertion.*;

public class SubscriberAssertions<U> implements ISubscriberAssertions<U> {

    private final ExecutionContext<?, ?, U> context;
    private final Subscriber<?, ?, U> testSubscriber;

    public SubscriberAssertions(ExecutionContext<?, ?, U> context, Subscriber<?, ?, U> testSubscriber) {
        this.context = context;
        this.testSubscriber = testSubscriber;
    }

    @Override
    public ObjectAssertion<U> event(int index) {
        return new ObjectAssertion<>(testSubscriber.event(index), this);
    }

    @Override
    public IntegerAssertion<U> eventCount() {
        return new IntegerAssertion<>(testSubscriber.eventCount(), this);
    }

    @Override
    public IntegerAssertion<U> completedCount() {
        return new IntegerAssertion<>(testSubscriber.completedCount(), this);
    }

    @Override
    public ClassAssertion<U> errorClass() {
        return new ClassAssertion<>(testSubscriber.errorClass(), this);
    }

    @Override
    public StringAssertion<U> errorMessage() {
        return new StringAssertion<>(testSubscriber.errorMessage(), this);
    }

    @Override
    public Then<U> and() {
        return new Then<>(context);
    }

}
