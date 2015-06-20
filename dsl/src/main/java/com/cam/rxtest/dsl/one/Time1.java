package com.cam.rxtest.dsl.one;

import java.time.Duration;

public interface Time1<T1, U> {

    When1<T1, U> advancesBy(Duration duration);
}
