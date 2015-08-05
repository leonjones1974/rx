package uk.camsw.rx.test.dsl.then;

import uk.camsw.rx.test.dsl.scenario.ExecutionContext;
import uk.camsw.rx.test.dsl.subscriber.SubscriberAssertions;

public class BaseThen<U> implements IThen<U> {

    private final ExecutionContext<?, ?, U, ?, ?> context;

    public BaseThen(ExecutionContext<?, ?, U, ?, ?> context) {
        this.context = context;
    }

    @Override
    public void executeCommands() {
        this.context.executeCommands();
    }

    @Override
    public SubscriberAssertions<U> subscriber(String id) {
        return new SubscriberAssertions<>(context, context.getSubscriber(id));
    }
}
