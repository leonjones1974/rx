package com.cam.rx.capture.instr;

import rx.Observer;
import rx.Subscriber;

public class SubscriberWrapper extends Subscriber {
    private final Observer observer;

    public SubscriberWrapper(Observer<?> observer) {

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
        System.out.println("WOOT WOOT OnNext: " + o);
        observer.onNext(o);
    }
}
