package uk.camsw.rx.test.dsl.base;


import uk.camsw.rx.test.dsl.impl.ExecutionContext;

public class When2<T1, T2, U> extends BaseWhen<T1, U, When2<T1, T2, U>> {

    private final ExecutionContext<T1, T2, U, Given2<T1, T2, U>, When2<T1, T2, U>> context;

    public When2(ExecutionContext<T1, T2, U, Given2<T1, T2, U>, When2<T1, T2, U>> context) {
        super(context);
        this.context = context;
    }

    public ISource<T1, When2<T1, T2, U>> source1() {
        return context.getSource1();
    }

    public ISource<T2, When2<T1, T2, U>> source2() {
        return context.getSource2();
    }
}
