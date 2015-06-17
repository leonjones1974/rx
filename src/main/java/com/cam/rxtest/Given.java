package com.cam.rxtest;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.functions.Func2;

import java.util.Map;

public class Given<T, U> {

    private final ExecutionContext<T, U> context;

    public Given(ExecutionContext<T, U> context) {
        this.context = context;
    }

    public Given<T, U> theStreamUnderTest(Func1<Observable<T>, Observable<U>> f) {
        Observable<U> sut = f.call(context.sourceAsObservable());
        context.setStreamUnderTest(sut);
        return this;
    }

    public Given<T, U> theStreamUnderTest2(Func1<Map<String, ? extends Observable<T>>, Observable<U>> f) {
        Observable<U> sut = f.call(context.allSourcesAsObservables());
        context.setStreamUnderTest(sut);
        return this;
    }

    public Given<T, U> theStreamUnderTest(Func2<Observable<T>, Scheduler, Observable<U>> f) {
        Observable<U> sut = f.call(context.sourceAsObservable(), context.getScheduler());
        context.setStreamUnderTest(sut);
        return this;
    }

    public When<T, U> when() {
        return new When<>(context);
    }

    public Given<T, U> aSourceStream(String id) {
        context.source(id);
        return new Given<>(context);
    }
}
