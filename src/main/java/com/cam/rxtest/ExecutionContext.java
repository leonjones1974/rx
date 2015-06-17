package com.cam.rxtest;

import com.google.common.collect.Maps;
import rx.Observable;
import rx.schedulers.TestScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Consumer;

public class ExecutionContext<T, U> {
    public static final String DEFAULT_SOURCE = "_default";
    private final Queue<Consumer<ExecutionContext<T, U>>> commands = new ArrayBlockingQueue<>(1000);
    private final Map<String, Subscriber<T, U>> subscribers = new HashMap<>();
    private final TestScheduler scheduler = new TestScheduler();
    private final Map<String, Source<T, U>> sources = new HashMap<>();

    private Observable<U> streamUnderTest;

    public Source<T, U> source() {
        return source(DEFAULT_SOURCE);
    }

    public Source<T, U> source(String id) {
        if (!sources.containsKey(id)) {
            sources.put(id, new Source<>(this));
        }
        return sources.get(id);
    }

    public void setStreamUnderTest(Observable<U> streamUnderTest) {
        this.streamUnderTest = streamUnderTest;
    }

    public Observable<U> getStreamUnderTest() {
        return streamUnderTest;
    }

    public void addCommand(Consumer<ExecutionContext<T, U>> command) {
        commands.offer(command);
    }

    public Subscriber<T, U> subscriber(String id) {
        if (!subscribers.containsKey(id)) subscribers.put(id, new Subscriber<>(id, this));
        return subscribers.get(id);
    }

    public void executeCommands() {
        while (!commands.isEmpty()) {
            commands.poll().accept(this);
        }
    }

    public TestScheduler getScheduler() {
        return scheduler;
    }

    public Map<String, ? extends Observable<T>> allSourcesAsObservables() {
        return Maps.transformValues(sources, Source::asObservable);
    }

    public Observable<T> sourceAsObservable() {
        return source().asObservable();
    }
}
