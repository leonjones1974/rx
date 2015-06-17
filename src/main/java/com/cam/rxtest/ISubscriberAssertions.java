package com.cam.rxtest;

public interface ISubscriberAssertions<U> {

    ObjectAssertion<U> event(int index);

    IntegerAssertion<U> eventCount();

    IntegerAssertion<U> completedCount();

    ClassAssertion<U> errorClass();

    StringAssertion<U> errorMessage();

    Then<U> and();
}
