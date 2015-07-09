package uk.camsw.rxtest.dsl.impl;

import uk.camsw.rxtest.dsl.one.Given1;
import uk.camsw.rxtest.dsl.two.Given2;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.functions.Func2;

public class Given<T1, T2, U>
        implements Given1<T1, U>,
        Given2<T1, T2, U> {

    private final ExecutionContext<T1, T2, U> context;

    public Given(ExecutionContext<T1, T2, U> context) {
        this.context = context;
    }

    public Given1<T1, U> createSubject(Func1<Observable<T1>, Observable<U>> f) {
        Observable<U> sut = f.call(context.getSource1().asObservable());
        context.setStreamUnderTest(sut);
        return this;
    }

    public Given1<T1, U> createSubjectWithScheduler(Func2<Observable<T1>, Scheduler, Observable<U>> f) {
        Observable<T1> source = context.getSource1().asObservable();
        Observable<U> sut = f.call(source, context.getScheduler());
        context.setStreamUnderTest(sut);
        return this;
    }

    @Override
    public Given2<T1, T2, U> createSubject(Func2<Observable<T1>, Observable<T2>, Observable<U>> f) {
        Observable<T1> source1 = context.getSource1().asObservable();
        Observable<T2> source2 = context.getSource2().asObservable();
        Observable<U> sut = f.call(source1, source2);
        context.setStreamUnderTest(sut);
        return this;
    }

    public When<T1, T2, U> when() {
        return new When<>(context);
    }

}
