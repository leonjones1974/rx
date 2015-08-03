package uk.camsw.rx.test;

import rx.subjects.PublishSubject;
import uk.camsw.rx.test.dsl.impl.ExecutionContext;
import uk.camsw.rx.test.dsl.impl.Given;
import uk.camsw.rx.test.dsl.one.Scenario1;
import uk.camsw.rx.test.dsl.two.Scenario2;

public class TestScenario<T1, T2, U>
        implements Scenario1<T1, U>,
        Scenario2<T1, T2, U> {

    private final ExecutionContext<T1, T2, U> context;

    public Given<T1, T2, U> given() {
        return new Given<>(context);
    }

    public TestScenario(ExecutionContext<T1, T2, U> context) {
        this.context = context;
    }

    public static <T1, U> Scenario1<T1, U> singleSource() {
        ExecutionContext<T1, Object, U> context = new ExecutionContext<>();
        return new TestScenario<>(context);
    }

    public static <T1, U> Scenario1<T1, U> singleSource(PublishSubject<T1> customSource) {
        ExecutionContext<T1, Object, U> context = new ExecutionContext<>(customSource);
        return new TestScenario<>(context);
    }

    public static <T1, T2, U> Scenario2<T1, T2, U> twoSources() {
        ExecutionContext<T1, T2, U> context = new ExecutionContext<>();
        return new TestScenario<>(context);
    }


}
