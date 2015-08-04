package uk.camsw.rx.test.dsl.impl;


import uk.camsw.rx.test.dsl.one.When1;
import uk.camsw.rx.test.dsl.two.When2;

public class When<T1, T2, U>
    implements When1<T1, U>,
        When2<T1, T2, U>
{

    private final ExecutionContext<T1, T2, U> context;

    public When(ExecutionContext<T1, T2, U> context) {
        this.context = context;
    }

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

    public Time<T1, T2, U> time() {
        return new Time<>(context);
    }

    @Override
    public Resource<T1, T2, U, ? extends AutoCloseable> resource(String id) {
        return context.getResource(id);
    }

    @Override
    public Source<T1, T1, T2, U> theSource() {
        return context.getSource1();
    }

    @Override
    public Subscriber<T1, T2, U> subscriber(String id) {
        return context.subscriber(id);
    }

    @Override
    public Source<T1, T1, T2, U> source1() {
        return context.getSource1();
    }

    @Override
    public Source<T2, T1, T2, U> source2() {
        return context.getSource2();
    }

}