package com.cam.rxtest;

import rx.Observable;
import rx.subjects.PublishSubject;

public class Source<T, U> {
    private final ExecutionContext<T, U> context;
    private final PublishSubject<T> publisher;

    public Source(ExecutionContext<T, U> context) {
        this.context = context;
        this.publisher = PublishSubject.create();
    }

    public When<T, U> emits(T event) {
        context.addCommand(c -> publisher.onNext(event));
        return new When<>(context);
    }

    public When<T, U> completes() {
        context.addCommand(c -> publisher.onCompleted());
        return new When<>(context);
    }

    public When<T, U> errors(Throwable t) {
        context.addCommand(c -> publisher.onError(t));
        return new When<>(context);
    }

    public Observable<T> asObservable() {
        return publisher;
    }
}
