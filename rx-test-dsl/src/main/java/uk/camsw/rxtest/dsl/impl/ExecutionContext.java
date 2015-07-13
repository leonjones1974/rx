package uk.camsw.rxtest.dsl.impl;

import rx.Observable;
import rx.schedulers.TestScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Consumer;

public class ExecutionContext<T1, T2, U> {

    private final Queue<Consumer<ExecutionContext<T1, T2, U>>> commands = new ArrayBlockingQueue<>(1000);
    private final Map<String, Subscriber<T1, T2, U>> subscribers = new HashMap<>();
    private final TestScheduler scheduler = new TestScheduler();

    private Observable<U> streamUnderTest;

    private Source<T1, T1, T2, U> source1 = new Source<>(this);
    private Source<T2, T1, T2, U> source2 = new Source<>(this);
    private boolean handleErrors = false;

    public Source<T1, T1, T2, U> getSource1() {
        return source1;
    }

    public Source<T2, T1, T2, U> getSource2() {
        return source2;
    }

    public void setStreamUnderTest(Observable<U> streamUnderTest) {
        this.streamUnderTest = streamUnderTest;
    }

    public Observable<U> getStreamUnderTest() {
        return streamUnderTest;
    }

    public void addCommand(Consumer<ExecutionContext<T1, T2, U>> command) {
        commands.offer(command);
    }

    public boolean handleErrors() {
        return handleErrors;
    }

    public void setHandleErrors(boolean handleErrors) {
        this.handleErrors = handleErrors;
    }

    public Subscriber<T1, T2, U> subscriber(String id) {
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


}
