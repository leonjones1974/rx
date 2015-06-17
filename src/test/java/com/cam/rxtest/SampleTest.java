package com.cam.rxtest;

import org.junit.Before;
import org.junit.Test;
import rx.Observable;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class SampleTest {

    private StreamTest<String, Integer> streamTest;

    @Before
    public void before() {
        streamTest = new StreamTest<>();
    }

    @Test
    public void simple() {
        Context<Integer> context = streamTest
                .subscribe("s1")
                .newEvent("1")
                .executeTest(this::subject);

        context.subscriber("s1").eventCount().isEqualTo(1);
        context.subscriber("s1").event(0).isEqualTo(2);
    }

    @Test
    public void multipleEvents() {
        Context<Integer> context = streamTest
                .subscribe("s1")
                .newEvent("1")
                .newEvent("2")
                .executeTest(this::subject);

        context.subscriber("s1").eventCount().isEqualTo(2);
    }

    @Test
    public void multipleSubscribers() {
        Context<Integer> context = streamTest
                .subscribe("s1")
                .newEvent("1")
                .subscribe("s2")
                .newEvent("2")
                .executeTest(this::subject);

        context.subscriber("s1").eventCount().isEqualTo(2);
        context.subscriber("s2").eventCount().isEqualTo(1);
    }

    @Test
    public void completion() {
        Context<Integer> context = streamTest
                .subscribe("s1")
                .newEvent("1")
                .completed()
                .executeTest(this::subject);

        context.subscriber("s1").completedCount().isEqualTo(1);
    }

    @Test
    public void error() {
        Context<Integer> context = streamTest
                .subscribe("s1")
                .newEvent("1")
                .error(new IllegalArgumentException("oh no"))
                .executeTest(this::subject);

        context.subscriber("s1").errorCount().isEqualTo(1);
        context.subscriber("s1").errorClass(0).isEqualTo(IllegalArgumentException.class);
        context.subscriber("s1").errorMessage(0).isEqualTo("oh no");
    }

    @Test
    public void temporal() {
        Context<List<String>> context = new StreamTest<String, List<String>>()
                .subscribe("s1")
                .newEvent("1a")
                .newEvent("1b")
                .newEvent("1c")
                .advanceTimeBy(Duration.ofSeconds(11))
                .newEvent("2a")
                .newEvent("2b")
                .completed()
                .executeTest((source, scheduler) -> source.buffer(10, TimeUnit.SECONDS, scheduler));

        context.subscriber("s1").eventCount().isEqualTo(2);
        context.subscriber("s1").event(0).isEqualTo(asList("1a", "1b", "1c"));
        context.subscriber("s1").event(1).isEqualTo(asList("2a", "2b"));
    }

    @Test
    public void unsubscribe() {
        Context<Integer> context = streamTest
                .subscribe("s1")
                .subscribe("s2")
                .newEvent("1")
                .unsubscribe("s1")
                .newEvent("2")
                .executeTest(this::subject);

        context.subscriber("s1").eventCount().isEqualTo(1);
        context.subscriber("s2").eventCount().isEqualTo(2);
    }

    private Observable<Integer> subject(Observable<String> source) {
        return source.map(s -> Integer.parseInt(s) + 1);
    }

}
