package uk.camsw.rx.test.dsl.base;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func2;
import rx.functions.Func3;
import uk.camsw.rx.test.dsl.impl.ExecutionContext;

public class Given2<T1, T2, U> extends BaseGiven<U, Given2<T1, T2, U>, When2<T1, T2, U>> {

    private final ExecutionContext<T1, T2, U, Given2<T1, T2, U>, When2<T1, T2, U>> context;

    public Given2(ExecutionContext<T1, T2, U, Given2<T1, T2, U>, When2<T1, T2, U>> context) {
        super(context);
        this.context = context;
    }

    @Override
    public When2<T1, T2, U> when() {
        return context.getWhen();
    }

    public Given2<T1, T2, U> subjectCreated(Func2<Observable<T1>, Observable<T2>, Observable<U>> f) {
        context.setStreamUnderTest(f.call(context.getSource1().asObservable(), context.getSource2().asObservable()));
        return this;
    }

    public Given2<T1, T2, U> subjectCreatedWithScheduler(Func3<Observable<T1>, Observable<T2>, Scheduler, Observable<U>> f) {
        context.setStreamUnderTest(f.call(context.getSource1().asObservable(), context.getSource2().asObservable(), context.getScheduler()));
        return this;
    }

}
