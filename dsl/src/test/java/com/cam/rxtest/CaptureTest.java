package com.cam.rxtest;

import com.cam.rx.capture.instr.CaptureAgent;
import com.cam.rx.capture.model.CaptureModel;
import com.cam.rxtest.dsl.one.Scenario1;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test must be run with the -javaagent:<path_to_capture_jar> flag
 */
public class CaptureTest {

    @Before
    public void before() {
    }

    @Test
    public void instrumentationShouldBeEnabled() {
        assertThat(CaptureAgent.initialized).isTrue();
    }

    @Test
    public void map() {
        Scenario1<Integer, String> scenario = TestScenario.singleSource();

        CaptureModel.instance().capturedStreams()
                .subscribe(s -> System.out.println("Got: " + s.getName()));

        scenario
                .given()
                    .createSubject(source -> source.map(s -> "hello" + s))
                .when()
                    .subscriber("s1").subscribes()
                    .theSource().emits(1)
                    .theSource().emits(2)
                    .theSource().emits(3)
                .then()
                    .subscriber("s1")
                        .eventCount().isEqualTo(3)
                        .event(0).isEqualTo("hello1");

    }

    @Test
    public void flatMap() {
        Scenario1<Integer, Integer> scenario = TestScenario.singleSource();

        CaptureModel.instance().capturedStreams()
                .subscribe(s -> System.out.println("Got: " + s.getName()));

        scenario
                .given()
                    .createSubject(source -> source.map(n -> n).flatMap(s -> Observable.just(1, 2, 3, 4)))
                .when()
                    .subscriber("s1").subscribes()
                    .theSource().emits(1)
                    .theSource().emits(2)
                .then()
                    .subscriber("s1")
                        .eventCount().isEqualTo(8);
    }

    @Test
    public void filter() {
        Scenario1<Integer, Integer> scenario = TestScenario.singleSource();

        CaptureModel.instance().capturedStreams()
                .subscribe(s -> System.out.println("Got: " + s.getName()));

        scenario
                .given()
                    .createSubject(source -> source.map(n -> n).filter(n -> n % 2 ==0))
                .when()
                    .subscriber("s1").subscribes()
                    .theSource().emits(1)
                    .theSource().emits(2)
                    .theSource().emits(3)
                    .theSource().emits(4)
                .then()
                    .subscriber("s1")
                        .eventCount().isEqualTo(2);
    }

}
