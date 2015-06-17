package com.cam.rxtest;

import java.util.HashMap;
import java.util.Map;

public class Context<T> {

    private Map<String, TestSubscriber<T>> subscribers = new HashMap<>();

    public TestSubscriber<T> subscriber(String id) {
        return subscribers.get(id);
    }

    public TestSubscriber<T> newSubscriber(String subscriberId) {
        TestSubscriber<T> subscriber = new TestSubscriber<>(subscriberId);
        subscribers.put(subscriberId, subscriber);
        return subscriber;
    }

    public void unsubscribe(String subscriberId) {
        subscribers.get(subscriberId).unsubscribe();
    }
}
