package uk.camsw.rx.test.dsl.time;

import uk.camsw.rx.test.dsl.when.IWhen;

import java.time.Duration;

public interface ITime<WHEN extends IWhen> {

    WHEN advancesBy(Duration duration);
}
