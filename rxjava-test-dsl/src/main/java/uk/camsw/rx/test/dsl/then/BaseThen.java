package uk.camsw.rx.test.dsl.then;

import uk.camsw.rx.test.dsl.KeyConstants;
import uk.camsw.rx.test.dsl.scenario.ExecutionContext;
import uk.camsw.rx.test.dsl.subscriber.SubscriberAssertions;

public class BaseThen<U> implements IThen<U> {

    private final ExecutionContext<?, ?, U, ?, ?> context;

    public BaseThen(ExecutionContext<?, ?, U, ?, ?> context) {
        this.context = context;
    }

    public void executeCommands() {
        this.context.executeCommands();
    }

    @Override
    public SubscriberAssertions<U> subscriber(int id) {
        return subscriber(String.valueOf(id));
    }

    @Override
    public SubscriberAssertions<U> subscriber(String id) {
        return new SubscriberAssertions<>(context, context.getSubscriber(id));
    }

    @Override
    public SubscriberAssertions<U> theSubscriber(String id) {
        return subscriber(id);
    }

    @Override
    public SubscriberAssertions<U> theSubscriber() {
        return subscriber(KeyConstants.THE_SUBSCRIBER);
    }

    @Override
    public SubscriberAssertions<U> theSubscribers() {
        return theSubscriber();
    }
}