package uk.camsw.rx.test.dsl.one;

import uk.camsw.rx.test.dsl.base.Given1;
import uk.camsw.rx.test.dsl.base.When1;
import uk.camsw.rx.test.dsl.impl.ExecutionContext;

public class Scenario1<T1, U> {

    private final ExecutionContext<T1, ?, U, Given1<T1, U>, When1<T1, U>> context;

    public Scenario1(ExecutionContext<T1, ?, U, Given1<T1, U>, When1<T1, U>> context) {
        this.context = context;
    }

    public Given1<T1, U> given() {
       return context.getGiven();
    }

}
