package com.cam.rxtest;

import com.cam.rxtest.dsl.one.Scenario1;
import org.junit.Test;
import rx.Observable;

public class MapTest {

//    @Test
//    public void itShould_InstrumentAllOperations() {
//        Scenario1<String, Integer> scenario = TestScenario.singleSource();
//        scenario
//                .given()
//                    .createSubject(source -> source.map(s -> "hello_" + s).flatMap(s -> Observable.just(1, 2, 3).scan((n, n1) -> n + n1)))
//                .when()
//                    .subscriber("s1").subscribes()
//                    .theSource().emits("a")
//                .then()
//                    .subscriber("s1")
//                        .event(0).isEqualTo(1)
//                        .event(1).isEqualTo(3);
//    }

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
