package uk.camsw.rx.test.dsl;

import rx.subjects.PublishSubject;
import uk.camsw.rx.test.dsl.scenario.DualSourceScenario;
import uk.camsw.rx.test.dsl.scenario.SingleSourceScenario;

public class TestScenario {

    public static <T1, U> SingleSourceScenario<T1, U> singleSource() {
        return new SingleSourceScenario<>();
    }

    public static <T1, T2, U> DualSourceScenario<T1, T2, U> twoSources() {
        return new DualSourceScenario<>();
    }

}
