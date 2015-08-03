package uk.camsw.rx.test.dsl.impl;

import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.core.ConditionFactory;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ExecutionContext<T1, T2, U> {

    private final Queue<Consumer<ExecutionContext<T1, T2, U>>> commands = new ArrayBlockingQueue<>(1000);
    private final Map<String, Subscriber<T1, T2, U>> subscribers = new HashMap<>();
    private final TestScheduler scheduler = new TestScheduler();

    private Observable<U> streamUnderTest;

    private final Source<T1, T1, T2, U> source1;
    private final Source<T2, T1, T2, U> source2;
    private boolean handleErrors = false;
    private Func1<U, String> renderer = Object::toString;
    private Duration asyncTimeoutDuration =  Duration.ofSeconds(5);

    public ExecutionContext() {
        source1 = new Source<>(this);
        source2 = new Source<>(this);
    }

    public ExecutionContext(PublishSubject<T1> customSource) {
        source1 = new Source<>(customSource, this);
        source2 = new Source<>(this);
    }

    public void setRenderer(Func1<U, String> renderer) {
        this.renderer = renderer;
    }

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

    public Duration getAsyncTimeoutDuration() {
        return asyncTimeoutDuration;
    }

    public ConditionFactory await() {
        com.jayway.awaitility.Duration timeToWait = new com.jayway.awaitility.Duration(asyncTimeoutDuration.toMillis(), TimeUnit.MILLISECONDS);
        return Awaitility.await().pollInterval(Math.min(100, asyncTimeoutDuration.toMillis() -1), TimeUnit.MILLISECONDS).atMost(timeToWait);
    }

    public Func1<U, String> getRenderer() {
        return renderer;
    }

    public void setAsyncTimeout(Duration duration) {
        if (duration.toMillis() < 2) this.asyncTimeoutDuration = Duration.ofMillis(2);
        else this.asyncTimeoutDuration = duration;
    }
}
