package uk.camsw.rx.test.dsl.assertion;

import uk.camsw.rx.test.dsl.base.IThen;

public interface ISubscriberAssertions<U> {

    ObjectAssertion<U> event(int index);

    IntegerAssertion<U> eventCount();

    IntegerAssertion<U> completedCount();

    ClassAssertion<U> errorClass();

    StringAssertion<U> errorMessage();

    BooleanAssertion<U> isErrored();

    IThen<U> and();

    StringAssertion<U> renderedStream();
}
