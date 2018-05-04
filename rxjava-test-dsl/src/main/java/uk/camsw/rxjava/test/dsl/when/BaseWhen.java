package uk.camsw.rxjava.test.dsl.when;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.functions.Action0;
import uk.camsw.rxjava.test.dsl.KeyConstants;
import uk.camsw.rxjava.test.dsl.scenario.ExecutionContext;
import uk.camsw.rxjava.test.dsl.subscriber.ISubscriber;
import uk.camsw.rxjava.test.dsl.then.BaseThen;
import uk.camsw.rxjava.test.dsl.then.IThen;
import uk.camsw.rxjava.test.dsl.time.BaseTime;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

public class BaseWhen<U, WHEN extends IWhen> implements IWhen<U, WHEN> {
    private static final Logger logger = LoggerFactory.getLogger(BaseWhen.class);

    private final ExecutionContext<?, ?, U, ?, WHEN> context;

    public BaseWhen(ExecutionContext<?, ?, U, ?, WHEN> context) {
        this.context = context;
    }

    @Override
    public ISubscriber<U, WHEN> subscriber(int id) {
        return subscriber(String.valueOf(id));
    }

    public ISubscriber<U, WHEN> subscriber(String id) {
        return context.getOrCreateSubscriber(id);
    }

    @Override
    public ISubscriber<U, WHEN> theSubscriber(String id) {
        return subscriber(id);
    }

    @Override
    public WHEN theCurrentThreadSleepsFor(long amount, TemporalUnit unit) {
        return theCurrentThreadSleepsFor(Duration.of(amount, unit));
    }

    @Override
    public WHEN sleepFor(long time, TemporalUnit unit) {
        return theCurrentThreadSleepsFor(time, unit);
    }

    @Override
    public WHEN sleepFor(Duration duration) {
        return theCurrentThreadSleepsFor(duration);

    }
    @Override
    public WHEN theCurrentThreadSleepsFor(Duration duration) {
        context.addCommand(ctx -> {
            logger.info("Sleeping current thread: [{}]", duration);
            try {
                Thread.sleep(duration.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        return context.getWhen();
    }

    @Override
    public ISubscriber<U, WHEN> theSubscriber() {
        return subscriber(KeyConstants.THE_SUBSCRIBER);
    }

    @Override
    public IThen<U> then() {
        BaseThen<U> then = new BaseThen<>(context);
        try {
            then.executeCommands();
        } finally {
            context.cleanUp();
        }
        return then;
    }

    @Override
    public void go() {
        then();
    }

    @Override
    public BaseTime<WHEN> time() {
        return new BaseTime<>(context);
    }

    @Override
    public WHEN actionIsPerformed(Action0 action) {
        context.addCommand(ctx -> action.call());
        return context.getWhen();
    }

    @Override
    public WHEN theActionIsPerformed(Action0 action) {
        return actionIsPerformed(action);
    }

}
