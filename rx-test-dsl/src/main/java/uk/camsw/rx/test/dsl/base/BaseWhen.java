package uk.camsw.rx.test.dsl.base;

import uk.camsw.rx.test.dsl.impl.ExecutionContext;
import uk.camsw.rx.test.dsl.impl.Then;

public class BaseWhen<T1, U, WHEN extends IWhen> implements IWhen<T1, U, BaseTime<WHEN>> {

    private final ExecutionContext<T1, ?, U, ?, WHEN> context;

    public BaseWhen(ExecutionContext<T1, ?, U, ?, WHEN> context) {
        this.context = context;
    }

    public ISubscriber<U, WHEN> subscriber(String id) {
        return context.getOrCreateSubscriber(id);
    }

    @Override
    public Then<U> then() {
        Then<U> then = new Then<>(context);
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
