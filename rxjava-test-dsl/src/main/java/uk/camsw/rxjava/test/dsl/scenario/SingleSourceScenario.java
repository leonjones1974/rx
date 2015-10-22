package uk.camsw.rxjava.test.dsl.scenario;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action0;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.PublishSubject;
import uk.camsw.rxjava.test.dsl.given.BaseGiven;
import uk.camsw.rxjava.test.dsl.source.ISource;
import uk.camsw.rxjava.test.dsl.when.BaseWhen;

public class SingleSourceScenario<T1, U> {

    private final ExecutionContext<T1, ?, U, Given<T1, U>, When<T1, U>> context;

    public SingleSourceScenario() {
        context  = new ExecutionContext<>();
        context.initSteps(new Given<>(context), new When<>(context));
    }

    public Given<T1, U> given() {
       return context.getGiven();
    }

    public static class Given<T1, U> extends BaseGiven<U, Given<T1, U>, When<T1, U>> {

        private final ExecutionContext<T1, ?, U, Given<T1, U>, When<T1, U>> context;

        public Given(ExecutionContext<T1, ?, U, Given<T1, U>, When<T1, U>> context) {
            super(context);
            this.context = context;
        }

        public Given<T1, U> theStreamUnderTest(Func1<Observable<T1>, Observable<? extends U>> f) {
            Observable<? extends U> sut = f.call(context.getSource1().asObservable());
            context.setStreamUnderTest(sut);
            return this;
        }

        public Given<T1, U> theStreamUnderTest(Func0<Observable<? extends U>> f) {
            Observable<? extends U> sut = f.call();
            context.setStreamUnderTest(sut);
            return this;
        }

        public Given<T1, U> theStreamUnderTest(Func2<Observable<T1>, Scheduler, Observable<? extends U>> f) {
            Observable<T1> source = context.getSource1().asObservable();
            Observable<? extends U> sut = f.call(source, context.getScheduler());
            context.setStreamUnderTest(sut);
            return this;
        }

        /**
         * @deprecated  Use {@link uk.camsw.rxjava.test.dsl.when.IWhen#actionIsPerformed(Action0)}
         */
        @Deprecated
        public Given<T1, U> theCustomSource(PublishSubject<T1> customSource) {
            context.setCustomSource1(customSource);
            return this;
        }

        @Override
        public When<T1, U> when() {
            return context.getWhen();
        }
    }

    public static class When<T1, U> extends BaseWhen<U, When<T1, U>> {

        private final ExecutionContext<T1, ?, U, ?, When<T1, U>> context;

        public When(ExecutionContext<T1, ?, U, ?, When<T1, U>> context) {
            super(context);
            this.context = context;
        }

        public ISource<T1, When<T1, U>> theSource() {
            return context.getSource1();
        }
    }
}
