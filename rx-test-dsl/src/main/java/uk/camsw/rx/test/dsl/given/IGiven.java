package uk.camsw.rx.test.dsl.given;

import rx.functions.Func1;
import uk.camsw.rx.test.dsl.when.IWhen;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

public interface IGiven<U, GIVEN extends IGiven, WHEN extends IWhen> {

    GIVEN errorsAreHandled();

    GIVEN renderer(Func1<U, String> renderer);

    GIVEN asyncTimeout(long timeout, TemporalUnit unit);

    GIVEN asyncTimeout(Duration duration);

    WHEN when();
}
