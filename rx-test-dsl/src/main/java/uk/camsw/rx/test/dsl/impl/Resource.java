package uk.camsw.rx.test.dsl.impl;

import rx.functions.Action1;
import uk.camsw.rx.test.dsl.one.Resource1;
import uk.camsw.rx.test.dsl.two.Resource2;

public class Resource<T1, T2, U, S extends AutoCloseable>
    implements Resource1<T1, U, S>,
        Resource2<T1, T2, U, S> {

    private final S inner;
    private final ExecutionContext<T1, T2, U> context;

    public Resource(S resource, ExecutionContext<T1, T2, U> context) {
        this.inner = resource;
        this.context = context;
    }

    @Override
    public Resource<T1, T2, U, S> does(Action1<S> action) {
        context.addCommand(context1 ->  action.call(inner));
        return this;
    }

    @Override
    public When<T1, T2, U> and() {
        return new When<>(context);
    }

    public void close() throws Exception {
        inner.close();
    }
}
