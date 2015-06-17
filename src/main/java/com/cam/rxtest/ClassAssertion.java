package com.cam.rxtest;

import org.assertj.core.api.AbstractClassAssert;

public class ClassAssertion<U> extends AbstractClassAssert<ClassAssertion<U>> implements ISubscriberAssertions<U> {
    private final ISubscriberAssertions<U> subscriberAssertions;

    public ClassAssertion(Class<?> actual, ISubscriberAssertions<U> subscriberAssertions) {
        super(actual, ClassAssertion.class);
        this.subscriberAssertions = subscriberAssertions;
    }

    @Override
    public ObjectAssertion<U> event(int index) {
        return subscriberAssertions.event(index);
    }

    @Override
    public IntegerAssertion<U> eventCount() {
        return subscriberAssertions.eventCount();
    }

    @Override
    public IntegerAssertion<U> completedCount() {
        return subscriberAssertions.completedCount();
    }

    @Override
    public ClassAssertion<U> errorClass() {
        return subscriberAssertions.errorClass();
    }

    @Override
    public StringAssertion<U> errorMessage() {
        return subscriberAssertions.errorMessage();
    }

    @Override
    public Then<U> and() {
        return subscriberAssertions.and();
    }
}
