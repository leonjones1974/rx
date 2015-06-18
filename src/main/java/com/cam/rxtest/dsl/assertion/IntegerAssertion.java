package com.cam.rxtest.dsl.assertion;

import com.cam.rxtest.dsl.impl.Then;
import org.assertj.core.api.AbstractIntegerAssert;

public class IntegerAssertion<U> extends AbstractIntegerAssert<IntegerAssertion<U>> implements ISubscriberAssertions<U> {

    private final ISubscriberAssertions<U> subscriberAssertions;

    public IntegerAssertion(int value, ISubscriberAssertions<U> subscriberAssertions) {
        super(value, IntegerAssertion.class);
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
    public Then<U> and() {
        return subscriberAssertions.and();
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
}
