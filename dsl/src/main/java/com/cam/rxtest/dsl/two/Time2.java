package com.cam.rxtest.dsl.two;

import java.time.Duration;

public interface Time2<T1, T2, U> {

    When2<T1, T2, U> advancesBy(Duration duration);
}