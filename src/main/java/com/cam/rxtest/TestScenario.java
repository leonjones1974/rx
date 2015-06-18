package com.cam.rxtest;

public class TestScenario<T1, T2, U>
        implements Scenario1<T1, U>,
        Scenario2<T1, T2, U> {

    private final ExecutionContext<T1, T2, U> context;

    public Given<T1, T2, U> given() {
        return new Given<>(context);
    }

    public TestScenario(ExecutionContext<T1, T2, U> context) {
        this.context = context;
    }

    public static <T1, U> Scenario1<T1, U> singleSource() {
        ExecutionContext<T1, Object, U> context = new ExecutionContext<>();
        return new TestScenario<>(context);
    }

    public static <T1, T2, U> Scenario2<T1, T2, U> twoSources() {
        ExecutionContext<T1, T2, U> context = new ExecutionContext<>();
        return new TestScenario<>(context);
    }
}
