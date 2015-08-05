package uk.camsw.rx.test.dsl.base;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.functions.Func2;
import uk.camsw.rx.test.dsl.impl.ExecutionContext;

public class Given1<T1, U> extends BaseGiven<U, Given1<T1, U>, When1<T1, U>> {

    private final ExecutionContext<T1, ?, U, Given1<T1, U>, When1<T1, U>> context;

    public Given1(ExecutionContext<T1, ?, U, Given1<T1, U>, When1<T1, U>> context) {
        super(context);
        this.context = context;
    }

    public Given1<T1, U> subjectCreated(Func1<Observable<T1>, Observable<U>> f) {
        Observable<U> sut = f.call(context.getSource1().asObservable());
        context.setStreamUnderTest(sut);
        return this;
    }

    //todo: can call this subject created, or aSubject or theSubject
    public Given1<T1, U> subjectCreatedWithScheduler(Func2<Observable<T1>, Scheduler, Observable<U>> f) {
        Observable<T1> source = context.getSource1().asObservable();
        Observable<U> sut = f.call(source, context.getScheduler());
        context.setStreamUnderTest(sut);
        return this;
    }

    @Override
    public When1<T1, U> when() {
        return new When1<>(context);
    }

}
