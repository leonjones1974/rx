package com.cam.rx.capture.instr;

import rx.Observer;
import rx.Subscriber;

public class SubscriberWrapper extends Subscriber {
    private final String name;
    private final Observer observer;

    public SubscriberWrapper(String name, Observer<?> observer) {
        this.name = name;
        this.observer = observer;
    }

    @Override
    public void onCompleted() {
        observer.onCompleted();
    }

    @Override
    public void onError(Throwable throwable) {
        observer.onError(throwable);
    }

    @Override
    public void onNext(Object o) {
        System.out.println("[" + name + "]" + " OnNext: " + o);
        observer.onNext(o);
    }
}
