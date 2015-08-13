package uk.camsw.rx.test.dsl.then;

import jdk.nashorn.internal.ir.LiteralNode;
import org.assertj.core.api.AbstractBooleanAssert;
import rx.functions.Action0;
import uk.camsw.rx.test.dsl.assertion.BooleanAssertion;
import uk.camsw.rx.test.dsl.subscriber.SubscriberAssertions;

import java.util.function.Consumer;

public interface IThen<U> {

    void executeCommands();

    SubscriberAssertions<U> subscriber(int id);
    SubscriberAssertions<U> subscriber(String id);
    SubscriberAssertions<U> theSubscriber(String id);   // Alias

    SubscriberAssertions<U> theSubscriber();
    SubscriberAssertions<U> theSubscribers();       // Alias

}
