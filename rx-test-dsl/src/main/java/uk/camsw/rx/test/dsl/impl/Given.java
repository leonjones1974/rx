package uk.camsw.rx.test.dsl.impl;

import rx.functions.Func0;
import uk.camsw.rx.test.dsl.two.Given2;
import uk.camsw.rx.test.dsl.one.Given1;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.functions.Func2;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

public class Given<T1, T2, U>
        implements Given1<T1, U>,
        Given2<T1, T2, U> {

    private final ExecutionContext<T1, T2, U> context;

    public Given(ExecutionContext<T1, T2, U> context) {
        this.context = context;
    }

    public Given<T1, T2, U> createSubject(Func1<Observable<T1>, Observable<U>> f) {
        Observable<U> sut = f.call(context.getSource1().asObservable());
        context.setStreamUnderTest(sut);
        return this;
    }

    public Given<T1, T2, U> createSubjectWithScheduler(Func2<Observable<T1>, Scheduler, Observable<U>> f) {
        Observable<T1> source = context.getSource1().asObservable();
        Observable<U> sut = f.call(source, context.getScheduler());
        context.setStreamUnderTest(sut);
        return this;
    }

    @Override
    public Given<T1, T2, U> createSubject(Func2<Observable<T1>, Observable<T2>, Observable<U>> f) {
        Observable<T1> source1 = context.getSource1().asObservable();
        Observable<T2> source2 = context.getSource2().asObservable();
        Observable<U> sut = f.call(source1, source2);
        context.setStreamUnderTest(sut);
        return this;
    }

    public When<T1, T2, U> when() {
        return new When<>(context);
    }

    @Override
    public Given<T1, T2, U> errorsAreHandled() {
        context.setHandleErrors(true);
        return this;
    }

    @Override
    public Given<T1, T2, U> renderer(Func1<U, String> renderer) {
        context.setRenderer(renderer);
        return this;
    }

    @Override
    public Given<T1, T2, U> asyncTimeout(long timeout, TemporalUnit unit) {
       return asyncTimeout(Duration.of(timeout, unit));
    }

    @Override
    public Given<T1, T2, U> asyncTimeout(Duration duration) {
        context.setAsyncTimeout(duration);
        return this;
    }

    @Override
    public Given<T1, T2, U> theResource(Func0<? extends AutoCloseable> f) {
        context.addCommand(context -> context.addResource(f.call()));
        return this;
    }

}
