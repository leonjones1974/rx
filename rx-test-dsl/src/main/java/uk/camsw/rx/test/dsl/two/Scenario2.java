package uk.camsw.rx.test.dsl.two;

import uk.camsw.rx.test.dsl.base.Given2;
import uk.camsw.rx.test.dsl.base.When2;
import uk.camsw.rx.test.dsl.impl.ExecutionContext;

public class Scenario2<T1, T2, U> {

    private final ExecutionContext<T1, T2, U, Given2<T1, T2, U>, When2<T1, T2, U>> context;

    public Scenario2(ExecutionContext<T1, T2, U, Given2<T1, T2, U>, When2<T1, T2, U>> context) {
        this.context = context;
    }

    public Given2<T1, T2, U> given() {
        return new Given2<>(context);
    }

}
