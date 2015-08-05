package uk.camsw.rx.test.dsl.base;

import java.time.Duration;

public interface ITime<WHEN extends IWhen> {

    WHEN advancesBy(Duration duration);
}
