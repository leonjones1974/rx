package uk.camsw.rx.test.dsl.then;

import uk.camsw.rx.test.dsl.subscriber.SubscriberAssertions;

public interface IThen<U> {

    void executeCommands();

    SubscriberAssertions<U> subscriber(int id);
    SubscriberAssertions<U> subscriber(String id);
    SubscriberAssertions<U> theSubscriber(String id);   // Alias

    SubscriberAssertions<U> theSubscriber();
    SubscriberAssertions<U> theSubscribers();       // Alias
}
