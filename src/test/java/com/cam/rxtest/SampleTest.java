package com.cam.rxtest;

import com.cam.rxtest.dsl.one.Scenario1;
import com.cam.rxtest.dsl.two.Scenario2;
import org.junit.Test;
import rx.Observable;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class SampleTest {

    @Test
    public void simple() {
        Scenario1<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                  .createSubject(source -> source.map(s -> Integer.parseInt(s) + 1))
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
        Scenario1<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                    .createSubject(source -> source.map(s -> Integer.parseInt(s) + 1))
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
        Scenario1<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                    .createSubject(source -> source.map(s -> Integer.parseInt(s) + 1))
                .when()
                    .subscriber("s1").subscribes()
                    .theSource().emits("1")
                    .subscriber("s1").unsubscribes()
                    .theSource().emits("2")
                .then()
                    .subscriber("s1")
                    .eventCount().isEqualTo(1);
    }


    @Test
    public void completion() {
        Scenario1<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .createSubject(source -> source.map(s -> Integer.parseInt(s) + 1))
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
        Scenario1<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                    .createSubject(source -> source.map(s -> Integer.parseInt(s) + 1))
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
        Scenario1<String, List<String>> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                    .createSubjectWithScheduler((source, scheduler) -> source.buffer(10, TimeUnit.SECONDS, scheduler))
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
        Scenario2<String, String, String> testScenario = TestScenario.twoSources();

        testScenario
                .given()
                    .createSubject(Observable::mergeWith)
                .when()
                    .subscriber("s1").subscribes()
                    .source1().emits("1")
                    .source2().emits("a")
                    .source1().emits("2")
                    .source2().emits("b")
                    .source1().completes()
                    .source2().completes()
                .then()
                    .subscriber("s1")
                        .eventCount().isEqualTo(4)
                        .event(0).isEqualTo("1")
                        .event(1).isEqualTo("a")
                        .event(2).isEqualTo("2")
                        .event(3).isEqualTo("b");
    }

    @Test
    public void zip() {
        Scenario2<String, Integer, String> testScenario = TestScenario.twoSources();

        testScenario
                .given()
                    .createSubject((s1, s2) -> s1.zipWith(s2, (z, n) -> z + n))
                .when()
                    .subscriber("s1").subscribes()
                    .source1().emits("a")
                    .source2().emits(1)
                    .source1().emits("b")
                    .source2().emits(2)
                    .source1().completes()
                    .source2().completes()
                .then()
                    .subscriber("s1")
                        .eventCount().isEqualTo(2)
                        .event(0).isEqualTo("a1")
                        .event(1).isEqualTo("b2");
    }
}
