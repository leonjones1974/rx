package com.cam.rxtest.dsl.impl;

import com.cam.rxtest.dsl.one.Source1;
import com.cam.rxtest.dsl.two.Source2;
import rx.Observable;
import rx.subjects.PublishSubject;

public class Source<T, T1, T2, U>
        implements Source1<T, T1, U>,
        Source2<T, T1, T2, U> {

    private final ExecutionContext<T1, T2, U> context;
    private final PublishSubject<T> publisher;

    public Source(ExecutionContext<T1, T2, U> context) {
        this.context = context;
        this.publisher = PublishSubject.create();
    }

    public When<T1, T2, U> emits(T event) {
        context.addCommand(c -> publisher.onNext(event));
        return new When<>(context);
    }

    public When<T1, T2, U> completes() {
        context.addCommand(c -> publisher.onCompleted());
        return new When<>(context);
    }

    public When<T1, T2, U> errors(Throwable t) {
        context.addCommand(c -> publisher.onError(t));
        return new When<>(context);
    }

    public Observable<T> asObservable() {
        return publisher;
    }

}