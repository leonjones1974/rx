package uk.camsw.rxjava.test.dsl.given;

import rx.functions.Func1;
import uk.camsw.rxjava.test.dsl.when.IWhen;
import uk.camsw.rxjava.test.dsl.scenario.ExecutionContext;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

public abstract class BaseGiven<U, GIVEN extends IGiven, WHEN extends IWhen> implements IGiven<U, GIVEN, WHEN> {

    private final ExecutionContext<?, ?, U, GIVEN, WHEN> context;

    protected BaseGiven(ExecutionContext<?, ?, U, GIVEN, WHEN> context) {
        this.context = context;
    }

    @Override
    public GIVEN errorsAreHandled() {
        context.setHandleErrors(true);
        return self();
    }

    @Override
    public GIVEN theRenderer(Func1<U, String> renderer) {
        context.setRenderer(renderer);
        return self();
    }

    @Override
    public GIVEN asyncTimeoutOf(long timeout, TemporalUnit unit) {
        return asyncTimeoutOf(Duration.of(timeout, unit));
    }

    @Override
    public GIVEN asyncTimeoutOf(Duration duration) {
        context.setAsyncTimeout(duration);
        return self();
    }

    private GIVEN self() {
        return (GIVEN) this;
    }
}
