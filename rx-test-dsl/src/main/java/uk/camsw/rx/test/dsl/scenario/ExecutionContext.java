package uk.camsw.rx.test.dsl.scenario;

import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.core.ConditionFactory;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;
import uk.camsw.rx.test.dsl.given.IGiven;
import uk.camsw.rx.test.dsl.source.BaseSource;
import uk.camsw.rx.test.dsl.source.ISource;
import uk.camsw.rx.test.dsl.subscriber.BaseSubscriber;
import uk.camsw.rx.test.dsl.when.IWhen;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ExecutionContext<T1, T2, U, GIVEN extends IGiven, WHEN extends IWhen> {

    private final Queue<Consumer<ExecutionContext<T1, T2, U, GIVEN, WHEN>>> commands = new ArrayBlockingQueue<>(1000);
    private final Map<String, BaseSubscriber<U, ? extends IWhen>> subscribers = new HashMap<>();
    private final TestScheduler scheduler = new TestScheduler();
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
    }

    public boolean handleErrors() {
        return handleErrors;
    }

    public void setHandleErrors(boolean handleErrors) {
        this.handleErrors = handleErrors;
    }

    public <WHEN extends IWhen> BaseSubscriber<U, WHEN> getOrCreateSubscriber(String id) {
        if (!subscribers.containsKey(id)) {
            ExecutionContext<?, ?, U, ?, WHEN> context = (ExecutionContext<?, ?, U, ?, WHEN>) this;
            BaseSubscriber<U, WHEN> subscriber = new BaseSubscriber<>(id, context);
            subscribers.put(id, subscriber);
        }
        return (BaseSubscriber<U, WHEN>) subscribers.get(id);
    }

    public BaseSubscriber<U, ? extends IWhen> getSubscriber(String id) {
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
}
