package com.cam.rxtest;

public class TestScenario<T, U> {

    private final ExecutionContext<T, U> context = new ExecutionContext<>();

    public Given<T, U> given() {
        return new Given<>(context);
    }
}
