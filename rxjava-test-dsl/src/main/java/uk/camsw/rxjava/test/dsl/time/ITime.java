package uk.camsw.rxjava.test.dsl.time;

import uk.camsw.rxjava.test.dsl.when.IWhen;

import java.time.Duration;

public interface ITime<WHEN extends IWhen> {

    WHEN advancesBy(Duration duration);
}
