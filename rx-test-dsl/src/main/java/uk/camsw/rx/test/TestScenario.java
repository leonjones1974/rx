package uk.camsw.rx.test;

import rx.subjects.PublishSubject;
import uk.camsw.rx.test.dsl.base.Given1;
import uk.camsw.rx.test.dsl.base.Given2;
import uk.camsw.rx.test.dsl.base.When1;
import uk.camsw.rx.test.dsl.base.When2;
import uk.camsw.rx.test.dsl.impl.ExecutionContext;
import uk.camsw.rx.test.dsl.one.Scenario1;
import uk.camsw.rx.test.dsl.two.Scenario2;

public class TestScenario {

    public static <T1, U> Scenario1<T1, U> singleSource() {
        ExecutionContext<T1, ?, U, Given1<T1, U>, When1<T1, U>> context = new ExecutionContext<>();
        context.initSteps(new Given1<>(context), new When1<>(context));
        return new Scenario1<>(context);
    }

    public static <T1, U> Scenario1<T1, U> singleSource(PublishSubject<T1> customSource) {
        ExecutionContext<T1, ?, U, Given1<T1, U>, When1<T1, U>> context = new ExecutionContext<>(customSource);
        context.initSteps(new Given1<>(context), new When1<>(context));
        return new Scenario1<>(context);
    }

    public static <T1, T2, U> Scenario2<T1, T2, U> twoSources() {
        ExecutionContext<T1, T2, U, Given2<T1, T2, U>, When2<T1, T2, U>> context = new ExecutionContext<>();
        context.initSteps(new Given2<>(context), new When2<>(context));
        return new Scenario2<>(context);
    }

}
