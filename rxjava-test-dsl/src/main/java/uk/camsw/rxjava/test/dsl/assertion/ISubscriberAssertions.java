package uk.camsw.rxjava.test.dsl.assertion;

import uk.camsw.rxjava.test.dsl.then.IThen;

public interface ISubscriberAssertions<U> {

    ObjectAssertion<U> event(int index);

    IntegerAssertion<U> eventCount();

    IntegerAssertion<U> completedCount();

    ClassAssertion<U> errorClass();

    StringAssertion<U> errorMessage();

    BooleanAssertion<U> isErrored();

    IThen<U> and();

    RenderedStreamAssertion<U> renderedStream();
}
