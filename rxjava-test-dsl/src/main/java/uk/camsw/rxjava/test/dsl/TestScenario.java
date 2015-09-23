package uk.camsw.rxjava.test.dsl;

import uk.camsw.rxjava.test.dsl.scenario.DualSourceScenario;
import uk.camsw.rxjava.test.dsl.scenario.SingleSourceScenario;

public class TestScenario {

    public static <T1, U> SingleSourceScenario<T1, U> singleSource() {
        return new SingleSourceScenario<>();
    }

    public static <T1, T2, U> DualSourceScenario<T1, T2, U> dualSources() {
        return new DualSourceScenario<>();
    }

}
