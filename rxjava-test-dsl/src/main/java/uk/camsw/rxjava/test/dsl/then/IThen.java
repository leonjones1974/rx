package uk.camsw.rxjava.test.dsl.then;

import uk.camsw.rxjava.test.dsl.subscriber.SubscriberAssertions;

public interface IThen<U> {

    SubscriberAssertions<U> subscriber(int id);
    SubscriberAssertions<U> subscriber(String id);
    SubscriberAssertions<U> theSubscriber(String id);   // Alias

    SubscriberAssertions<U> theSubscriber();
    SubscriberAssertions<U> theSubscribers();       // Alias

}
