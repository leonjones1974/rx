package com.cam.rxtest;

import rx.Observer;
import rx.observers.TestSubscriber;

import java.util.Observable;

public class EventSource<T> {

    public EventSource emit(T event) {
        return null;
    }

    public Context<T> go() {
        return null;
    }

    public EventSource<T> subscribe(String subscriberId) {
        return this;
    }
}
