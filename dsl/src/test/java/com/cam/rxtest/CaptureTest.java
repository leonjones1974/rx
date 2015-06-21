package com.cam.rxtest;

import com.cam.rx.capture.instr.CaptureAgent;
import com.cam.rxtest.dsl.one.Scenario1;
import org.junit.Before;
import org.junit.Test;

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
    public void itShould_CaptureAMappedStream() {
        Scenario1<Integer, String> scenario = TestScenario.singleSource();

        scenario
                .given()
                    .createSubject(source -> source.map(s -> "hello" + s).map(s -> s.toUpperCase()))
                .when()
                    .subscriber("s1").subscribes()
                    .theSource().emits(1)
                    .theSource().emits(2)
                    .theSource().emits(3)
                .then()
                    .subscriber("s1")
                        .eventCount().isEqualTo(3)
                        .event(0).isEqualTo("HELLO1")
                        .event(1).isEqualTo("HELLO2")
                        .event(2).isEqualTo("HELLO3");


    }

}
