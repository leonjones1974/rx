package uk.camsw.rx.test.dsl.time;

import uk.camsw.rx.test.dsl.scenario.ExecutionContext;
import uk.camsw.rx.test.dsl.when.IWhen;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class BaseTime<WHEN extends IWhen> implements ITime<WHEN> {

    private final ExecutionContext<?, ?, ?, ?, WHEN> context;

    public BaseTime(ExecutionContext<?, ?, ?, ?, WHEN> context) {
        this.context = context;
    }

    @Override
    public WHEN advancesBy(Duration duration) {
        this.context.addCommand(c -> {
            c.getScheduler().advanceTimeBy(duration.toNanos(), TimeUnit.NANOSECONDS);
            c.executeCommands();
        });
       return context.getWhen();
    }
}
