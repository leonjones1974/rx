package uk.camsw.rx.test.dsl.impl;

public class Then<U> {

    private final ExecutionContext<?, ?, U> context;

    public Then(ExecutionContext<?, ?,  U> context) {
        this.context = context;
    }

    public void executeCommands()  {
        this.context.executeCommands();
    }

    public SubscriberAssertions<U> subscriber(String id) {
        return new SubscriberAssertions<>(context, context.subscriber(id));
    }
}
