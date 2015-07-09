package uk.camsw.rxtest.dsl.impl;

import uk.camsw.rxtest.dsl.one.Time1;
import uk.camsw.rxtest.dsl.two.Time2;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class Time<T1, T2, U>
        implements Time1<T1, U>,
        Time2<T1, T2, U> {

    private final ExecutionContext<T1, T2, U> context;

    public Time(ExecutionContext<T1, T2, U> context) {
        this.context = context;
    }

    public When<T1, T2, U> advancesBy(Duration duration) {
        this.context.addCommand(c -> {
            c.getScheduler().advanceTimeBy(duration.toNanos(), TimeUnit.NANOSECONDS);
            c.executeCommands();
        });
        return new When<>(context);
    }
}
