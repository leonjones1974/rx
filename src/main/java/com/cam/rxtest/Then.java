package com.cam.rxtest;

public class Then<U> {

    private final ExecutionContext<?, U> context;

    public Then(ExecutionContext<?, U> context) {
        this.context = context;
        this.context.executeCommands();
    }

    public SubscriberAssertions<U> subscriber(String id) {
        return new SubscriberAssertions<>(context, context.subscriber(id));
    }
}
