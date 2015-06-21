package com.cam.rxtest;

import com.cam.rxtest.dsl.one.Scenario1;
import org.junit.Test;

public class MapTest {

    @Test
    public void itShould_InstrumentSimpleMap() {
        Scenario1<String, String> scenario = TestScenario.singleSource();
        scenario
                .given()
                    .createSubject(source -> source.map(s -> "hello"))
                .when()
                    .subscriber("s1").subscribes()
                    .theSource().emits("a")
                .then()
                    .subscriber("s1")
                    .event(0).isEqualTo("hello");

    }

}
