package com.cam.rxtest;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

public class StreamTest<T, T1> {

    private Context<T1> context = new Context<>();
    private final Queue<Runnable> commands = new ArrayBlockingQueue<>(1000);
    private PublishSubject<T> source = PublishSubject.create();
    private Observable<T1> subject;
    private TestScheduler scheduler = new TestScheduler();

    public StreamTest<T, T1> subscribe(String subscriberId) {
        commands.offer(() -> {
            TestSubscriber<T1> subscriber = context.newSubscriber(subscriberId);
            subscriber.subscribeTo(subject.subscribeOn(scheduler).observeOn(scheduler));
            scheduler.triggerActions();
        });
        return this;
    }

    public Context<T1> executeTest(Function<Observable<T>, Observable<T1>> f) {
        subject = f.apply(source);
        executeCommands();
        return context;
    }

    public Context<T1> executeTest(BiFunction<Observable<T>, Scheduler, Observable<T1>> f) {
        subject = f.apply(source, scheduler);
        executeCommands();
        return context;
    }

    private void executeCommands() {
        while(commands.size() > 0) {
            commands.poll().run();
        }
    }

    public StreamTest<T, T1> newEvent(T event) {
        commands.offer(() -> {
            source.onNext(event);
            scheduler.triggerActions();
        });
        return this;
    }

    public StreamTest<T, T1> completed() {
        commands.offer(() -> {
            source.onCompleted();
            scheduler.triggerActions();
        });
        return this;
    }

    public StreamTest<T, T1> error(Throwable e) {
        commands.offer(() -> {
            source.onError(e);
            scheduler.triggerActions();
        });
        return this;
    }

    public StreamTest<T, T1> advanceTimeBy(long timespan, TimeUnit timeUnit) {
        commands.offer(() -> {
            scheduler.advanceTimeBy(timespan, timeUnit);
            scheduler.triggerActions();
        });
        return this;
    }

    public StreamTest<T, T1> advanceTimeBy(Duration duration) {
        return advanceTimeBy(duration.toNanos(), TimeUnit.NANOSECONDS);
    }

    public StreamTest<T, T1> unsubscribe(String subscriberId) {
        commands.offer(() -> context.unsubscribe(subscriberId));
        return this;
    }
}
