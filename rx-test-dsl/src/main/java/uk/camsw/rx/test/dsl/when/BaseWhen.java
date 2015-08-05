package uk.camsw.rx.test.dsl.when;

import uk.camsw.rx.test.dsl.then.BaseThen;
import uk.camsw.rx.test.dsl.time.BaseTime;
import uk.camsw.rx.test.dsl.subscriber.ISubscriber;
import uk.camsw.rx.test.dsl.then.IThen;
import uk.camsw.rx.test.dsl.scenario.ExecutionContext;

public class BaseWhen<U, WHEN extends IWhen> implements IWhen<U, WHEN> {

    private final ExecutionContext<?, ?, U, ?, WHEN> context;

    public BaseWhen(ExecutionContext<?, ?, U, ?, WHEN> context) {
        this.context = context;
    }

    public ISubscriber<U, WHEN> subscriber(String id) {
        return context.getOrCreateSubscriber(id);
    }

    @Override
    public IThen<U> then() {
        BaseThen<U> then = new BaseThen<>(context);
        context.addCommand(ExecutionContext::cleanUp);
        then.executeCommands();
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

}
