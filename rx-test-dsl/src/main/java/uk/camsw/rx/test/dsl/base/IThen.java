package uk.camsw.rx.test.dsl.base;

import uk.camsw.rx.test.dsl.impl.SubscriberAssertions;

public interface IThen<U> {

    void executeCommands();

    SubscriberAssertions<U> subscriber(String id);
}
