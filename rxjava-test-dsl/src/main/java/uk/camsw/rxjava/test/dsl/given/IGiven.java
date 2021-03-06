package uk.camsw.rxjava.test.dsl.given;

import rx.functions.Func1;
import uk.camsw.rxjava.test.dsl.when.IWhen;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

public interface IGiven<U, GIVEN extends IGiven, WHEN extends IWhen> {

    GIVEN errorsAreHandled();

    GIVEN theRenderer(Func1<U, String> renderer);

    GIVEN asyncTimeoutOf(long timeout, TemporalUnit unit);

    GIVEN asyncTimeoutOf(Duration duration);

    WHEN when();
}
