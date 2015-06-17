package com.cam.rxtest;

import org.junit.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class SampleTest {

    @Test
    public void simple() {
        TestScenario<String, Integer> testScenario = new TestScenario<>();

        testScenario
                .given()
                    .theStreamUnderTest(source -> source.map(s -> Integer.parseInt(s) + 1))
                .when()
                    .subscriber("s1").subscribes()
                    .theSource().emits("1")
                    .theSource().emits("2")
                    .theSource().completes()
                .then()
                    .subscriber("s1")
                        .eventCount().isEqualTo(2)
                        .event(0).isEqualTo(2)
                        .event(1).isEqualTo(3);
    }

    @Test
    public void multipleSubscribers() {
        TestScenario<String, Integer> testScenario = new TestScenario<>();

        testScenario
                .given()
                    .theStreamUnderTest(source -> source.map(s -> Integer.parseInt(s) + 1))
                .when()
                    .subscriber("s1").subscribes()
                    .theSource().emits("1")
                    .subscriber("s2").subscribes()
                    .theSource().emits("2")
                .then()
                    .subscriber("s1")
                    .event(0).isEqualTo(2)
                    .event(1).isEqualTo(3)
                    .eventCount().isEqualTo(2)
                .and()
                    .subscriber("s2")
                    .event(0).isEqualTo(3)
                    .eventCount().isEqualTo(1);
    }

    @Test
    public void unsubscribe() {
        TestScenario<String, Integer> testScenario = new TestScenario<>();

        testScenario
                .given()
                    .theStreamUnderTest(source -> source.map(s -> Integer.parseInt(s) + 1))
                .when()
                    .subscriber("s1").subscribes()
                    .theSource().emits("1")
                    .subscriber("s1").unsubscribes()
                    .theSource().emits("2")
                .then()
                    .subscriber("s1")
                    .eventCount().isEqualTo(1);
    }


//    @Test
//    public void zip() {
//        Observable<String> s1;
//        Observable<String> s2;
//
//        testScenario.newSource("stream1", String.class);
//        testScenario.newSource("stream2", Integer.class);
//
//        testScenario
//                .given(aStream("stream1", String.class))
//                .and(aStream("stream2", Integer.class))
//                .and(theStreamUnderTest((Observable<?>[] sources) -> {
//                    Observable.zip(sources[0], sources[1], new Func2<String, Integer, String>() {
//                        @Override
//                        public String call(String o, Integer o2) {
//                            return null;
//                        }
//                    });
//                })
//                        .when(subscriber("s1").subscribes())
//                        .and(stream("stream1").emitsEvent("a"))
//                        .and(stream("stream2").emitsEvent(1))
//                        .then()
//                        .subscriber("s1").eventCount().isEqualTo(1)
//                        .subscriber("s2").event(0).isEqualTo("a1");
//
//        Context<Integer> context = testScenario
//                .newSource("source2")
//                .newSubscriber("s1")
//                .newEvent("1")
//                .source("source2").newEvent("2")
//                .newEvent("3")
//                .source("source2").newEvent("4")
//                .executeTest(this::subject);
//
//        context.subscriber("s1").eventCount().isEqualTo(2);
//        context.subscriber("s2").eventCount().isEqualTo(1);
//    }

    @Test
    public void completion() {
        TestScenario<String, Integer> testScenario = new TestScenario<>();

        testScenario
                .given()
                .theStreamUnderTest(source -> source.map(s -> Integer.parseInt(s) + 1))
                .when()
                .subscriber("s1").subscribes()
                .theSource().emits("1")
                .theSource().completes()
                .then()
                .subscriber("s1")
                .completedCount().isEqualTo(1);
    }

    @Test
    public void error() {
        TestScenario<String, Integer> testScenario = new TestScenario<>();

        testScenario
                .given()
                    .theStreamUnderTest(source -> source.map(s -> Integer.parseInt(s) + 1))
                .when()
                    .subscriber("s1").subscribes()
                    .theSource().emits("1")
                    .theSource().errors(new IllegalArgumentException("oh no"))
                .then()
                    .subscriber("s1")
                        .errorClass().isEqualTo(IllegalArgumentException.class)
                        .errorMessage().isEqualTo("oh no");
    }

    @Test
    public void temporal() {
        TestScenario<String, List<String>> testScenario = new TestScenario<>();

        testScenario
                .given()
                    .theStreamUnderTest((source, scheduler) -> source.buffer(10, TimeUnit.SECONDS, scheduler))
                .when()
                    .subscriber("s1").subscribes()
                    .theSource().emits("1a")
                    .theSource().emits("1b")
                    .theSource().emits("1c")
                    .time().advancesBy(Duration.ofSeconds(11))
                    .theSource().emits("2a")
                    .theSource().emits("2b")
                    .theSource().completes()
                .then()
                    .subscriber("s1")
                        .eventCount().isEqualTo(2)
                        .event(0).isEqualTo(asList("1a", "1b", "1c"))
                        .event(1).isEqualTo(asList("2a", "2b"));
    }

    @Test
    public void merge() {
        TestScenario<String, String> testScenario = new TestScenario<>();

        testScenario
                .given()
                    .aSourceStream("s1")
                    .aSourceStream("s2")
                    .theStreamUnderTest2(sources -> sources.get("s1").mergeWith(sources.get("s2")))
                .when()
                    .subscriber("s1").subscribes()
                    .theSource("s1").emits("1")
                    .theSource("s2").emits("a")
                    .theSource("s1").emits("2")
                    .theSource("s2").emits("b")
                    .theSource("s1").completes()
                    .theSource("s2").completes()
                .then()
                    .subscriber("s1")
                        .eventCount().isEqualTo(4)
                        .event(0).isEqualTo("1")
                        .event(1).isEqualTo("a")
                        .event(2).isEqualTo("2")
                        .event(3).isEqualTo("b");
    }
}
