package com.cam.rxtest.dsl.assertion;

import com.cam.rxtest.dsl.impl.Then;

public interface ISubscriberAssertions<U> {

    ObjectAssertion<U> event(int index);

    IntegerAssertion<U> eventCount();

    IntegerAssertion<U> completedCount();

    ClassAssertion<U> errorClass();

    StringAssertion<U> errorMessage();

    Then<U> and();
}
