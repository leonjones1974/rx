package com.cam.rxtest;


public class When<T, U> {

    private final ExecutionContext<T, U> context;

    public When(ExecutionContext<T, U> context) {
        this.context = context;
    }

    public Source<T, U> theSource() {
        return context.source();
    }

    public Subscriber<T, U> subscriber(String id) {
        return context.subscriber(id);
    }

    public Then<U> then() {
        return new Then<>(context);
    }

    public Time<T, U> time() {
        return new Time<>(context);
    }

    public Source<T, U> theSource(String id) {
        return context.source(id);
    }
}
