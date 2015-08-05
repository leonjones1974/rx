package uk.camsw.rx.test.dsl.then;

import uk.camsw.rx.test.dsl.assertion.SubscriberAssertions;

public interface IThen<U> {

    void executeCommands();

    SubscriberAssertions<U> subscriber(String id);
}
