package uk.camsw.rx.test.dsl.base;

import uk.camsw.rx.test.dsl.impl.ExecutionContext;

public class When1<T1, U> extends BaseWhen<U, When1<T1, U>> {

    private final ExecutionContext<T1, ?, U, ?, When1<T1, U>> context;

    public When1(ExecutionContext<T1, ?, U, ?, When1<T1, U>> context) {
        super(context);
        this.context = context;
    }

    public ISource<T1, When1<T1, U>> theSource() {
        return context.getSource1();
    }
}
