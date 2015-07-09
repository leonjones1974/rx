package com.cam.rxtest;

import com.cam.rx.capture.instr.CaptureAgent;
import com.cam.rx.capture.model.CaptureModel;
import com.cam.rx.capture.model.Event;
import com.cam.rx.capture.model.Stream;
import com.cam.rxtest.dsl.one.Scenario1;
import com.google.common.base.Strings;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test must be run with the -javaagent:<path_to_capture_jar> flag
 */
public class CaptureTest {

    @Test
    public void instrumentationShouldBeEnabled() {
        assertThat(CaptureAgent.initialized).isTrue();
    }

    @Test
    public void map() {
        Scenario1<Integer, String> scenario = TestScenario.singleSource();

        Subscription captureSubscription = CaptureModel.instance().beginCapture();
        scenario
                .given()
                    .createSubject(source -> source
                           .map(n -> n * 2)
                           .scan((n1, n2) -> n1 + n2)
                            .map(z -> "z: " + z.toString())
                            .flatMap(z -> Observable.just("a", "b", "c")).filter(z -> !z.equals("b"))
        )
                .when()
                    .subscriber("s1").subscribes()
                    .theSource().emits(1)
                    .theSource().emits(2)
                    .theSource().emits(3)
                .then()
                    .subscriber("s1")
                .eventCount().isEqualTo(6);

        captureSubscription.unsubscribe();

        CaptureModel.instance()
                .streams()
                .doOnNext(s -> System.out.println(s.getName()))
                .flatMap(Stream::events)
                .subscribe(s -> System.out.println("\t" + s.getValue()));

    }

    @Test
    public void flatMap() {
        Scenario1<Integer, Integer> scenario = TestScenario.singleSource();

        scenario
                .given()
                .createSubject(source -> source.map(n -> n).flatMap(s -> Observable.just(1, 2, 3, 4)).filter(n -> n % 2 == 0))
                .when()
                .subscriber("s1").subscribes()
                .theSource().emits(1)
                .theSource().emits(2)
                .then()
                .subscriber("s1")
                .eventCount().isEqualTo(4);

    }

    @Test
    public void filter() {
        Scenario1<Integer, Integer> scenario = TestScenario.singleSource();

        scenario
                .given()
                .createSubject(source -> source.map(n -> n).filter(n -> n % 2 == 0))
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
