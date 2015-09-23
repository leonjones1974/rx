package uk.camsw.rxjava.test.dsl.scenario;

import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.core.ConditionFactory;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;
import uk.camsw.rxjava.test.dsl.given.IGiven;
import uk.camsw.rxjava.test.dsl.source.BaseSource;
import uk.camsw.rxjava.test.dsl.source.ISource;
import uk.camsw.rxjava.test.dsl.subscriber.BaseSubscriber;
import uk.camsw.rxjava.test.dsl.subscriber.ISubscriber;
import uk.camsw.rxjava.test.dsl.when.IWhen;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ExecutionContext<T1, T2, U, GIVEN extends IGiven, WHEN extends IWhen> {

    private final Queue<Consumer<ExecutionContext<T1, T2, U, GIVEN, WHEN>>> commands = new ArrayBlockingQueue<>(1000);
    private final Map<String, ISubscriber<U, WHEN>> subscribers = new HashMap<>();
    private final List<Action1<ExecutionContext<T1, T2, U, GIVEN, WHEN>>> finalActions = new ArrayList<>();
    private final Map<String, Object> customProperties = new HashMap<>();

    private TestScheduler scheduler = new TestScheduler();
    private GIVEN given;
    private WHEN when;

    private Observable<U> streamUnderTest;

    private BaseSource<T1, GIVEN, WHEN> source1;
    private BaseSource<T2, GIVEN, WHEN> source2;

    private boolean handleErrors = false;
    private Func1<U, String> renderer = Object::toString;
    private Duration asyncTimeoutDuration = Duration.ofSeconds(5);

    public ExecutionContext() {
        source1 = new BaseSource<>(this);
        source2 = new BaseSource<>(this);
    }

    public void initSteps(GIVEN given, WHEN when) {
        this.given = given;
        this.when = when;
    }

    public void setCustomSource1(PublishSubject<T1> customSource) {
        source1 = new BaseSource<>(customSource, this);
    }

    public void setCustomSource2(PublishSubject<T2> customSource) {
        source2 = new BaseSource<>(customSource, this);
    }

    public void setRenderer(Func1<U, String> renderer) {
        this.renderer = renderer;
    }

    public ISource<T1, WHEN> getSource1() {
        return source1;
    }

    public ISource<T2, WHEN> getSource2() {
        return source2;
    }

    public void setStreamUnderTest(Observable<U> streamUnderTest) {
        this.streamUnderTest = streamUnderTest;
    }

    public Observable<U> getStreamUnderTest() {
        return streamUnderTest;
    }

    public void addCommand(Consumer<ExecutionContext<T1, T2, U, GIVEN, WHEN>> command) {
        commands.offer(command);
    }

    public void cleanUp() {
        /* todo: Think about the lifecycle more - this currently happens after then, rather than at end of assertions (there is no end).
            That leaves the state of the context a little non-deterministic in that we can't really clean-up everything as assertions may reference stuff
            Example: If a subscriber assertion used a topic that had been released as part of a final action, it would be closed before the assertion got it
        */

        finalActions.forEach(a -> a.call(this));
    }

    public boolean handleErrors() {
        return handleErrors;
    }

    public void setHandleErrors(boolean handleErrors) {
        this.handleErrors = handleErrors;
    }

    public ISubscriber<U, WHEN> getOrCreateSubscriber(String id) {
        if (!subscribers.containsKey(id)) {
            BaseSubscriber<U, WHEN> subscriber = new BaseSubscriber<>(id, this);
            subscribers.put(id, subscriber);
        }
        return subscribers.get(id);
    }

    public ISubscriber<U, WHEN> getSubscriber(String id) {
        return subscribers.get(id);
    }

    public void executeCommands() {
        while (!commands.isEmpty()) commands.poll().accept(this);
    }

    public void addFinally(Action1<ExecutionContext<T1, T2, U, GIVEN, WHEN>> f) {
        finalActions.add(f);
    }

    public TestScheduler getScheduler() {
        return scheduler;
    }

    public Duration getAsyncTimeoutDuration() {
        return asyncTimeoutDuration;
    }

    public ConditionFactory await() {
        com.jayway.awaitility.Duration timeToWait = new com.jayway.awaitility.Duration(asyncTimeoutDuration.toMillis(), TimeUnit.MILLISECONDS);
        return Awaitility.await().pollInterval(Math.min(100, asyncTimeoutDuration.toMillis() - 1), TimeUnit.MILLISECONDS).atMost(timeToWait);
    }

    public Func1<U, String> getRenderer() {
        return renderer;
    }

    public void setAsyncTimeout(Duration duration) {
        if (duration.toMillis() < 2) this.asyncTimeoutDuration = Duration.ofMillis(2);
        else this.asyncTimeoutDuration = duration;
    }

    public WHEN getWhen() {
        return when;
    }

    public GIVEN getGiven() {
        return given;
    }

    public void put(String key, Object object) {
        if (customProperties.containsKey(key)) throw new IllegalArgumentException("Existing values cannot be overwritten");
        customProperties.put(key, object);
    }

    public <O> O get(String id) {
        return (O) customProperties.get(id);
    }
}
