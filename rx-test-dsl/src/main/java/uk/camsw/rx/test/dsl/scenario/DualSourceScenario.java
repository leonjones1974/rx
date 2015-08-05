package uk.camsw.rx.test.dsl.scenario;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.subjects.PublishSubject;
import uk.camsw.rx.test.dsl.given.BaseGiven;
import uk.camsw.rx.test.dsl.source.ISource;
import uk.camsw.rx.test.dsl.when.BaseWhen;

public class DualSourceScenario<T1, T2, U> {

    private final ExecutionContext<T1, T2, U, Given<T1, T2, U>, When<T1, T2, U>> context;

    public DualSourceScenario() {
        context = new ExecutionContext<>();
        context.initSteps(new Given<>(context), new When<>(context));
    }

    public Given<T1, T2, U> given() {
        return new Given<>(context);
    }

    public static class Given<T1, T2, U> extends BaseGiven<U, Given<T1, T2, U>, When<T1, T2, U>> {

        private final ExecutionContext<T1, T2, U, Given<T1, T2, U>, When<T1, T2, U>> context;

        public Given(ExecutionContext<T1, T2, U, Given<T1, T2, U>, When<T1, T2, U>> context) {
            super(context);
            this.context = context;
        }

        @Override
        public When<T1, T2, U> when() {
            return context.getWhen();
        }

        public Given<T1, T2, U> theStreamUnderTest(Func2<Observable<T1>, Observable<T2>, Observable<U>> f) {
            context.setStreamUnderTest(f.call(context.getSource1().asObservable(), context.getSource2().asObservable()));
            return this;
        }

        public Given<T1, T2, U> theStreamUnderTest(Func3<Observable<T1>, Observable<T2>, Scheduler, Observable<U>> f) {
            context.setStreamUnderTest(f.call(context.getSource1().asObservable(), context.getSource2().asObservable(), context.getScheduler()));
            return this;
        }

        public Given<T1, T2, U> theCustomSource1(PublishSubject<T1> customSource) {
            context.setCustomSource1(customSource);
            return this;
        }

        public Given<T1, T2, U> theCustomSource2(PublishSubject<T2> customSource) {
            context.setCustomSource2(customSource);
            return this;
        }
    }

    public static class When<T1, T2, U> extends BaseWhen<U, When<T1, T2, U>> {

        private final ExecutionContext<T1, T2, U, Given<T1, T2, U>, When<T1, T2, U>> context;

        public When(ExecutionContext<T1, T2, U, Given<T1, T2, U>, When<T1, T2, U>> context) {
            super(context);
            this.context = context;
        }

        public ISource<T1, When<T1, T2, U>> source1() {
            return context.getSource1();
        }

        public ISource<T2, When<T1, T2, U>> source2() {
            return context.getSource2();
        }
    }

}
