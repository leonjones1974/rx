package com.cam.rxtest;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class Time<T, U> {

    private final ExecutionContext<T, U> context;

    public Time(ExecutionContext<T, U> context) {
        this.context = context;
    }

    public When<T, U> advancesBy(Duration duration) {
        this.context.addCommand(c -> {
            c.getScheduler().advanceTimeBy(duration.toNanos(), TimeUnit.NANOSECONDS);
            c.executeCommands();
        });
        return new When<>(context);
    }
}
