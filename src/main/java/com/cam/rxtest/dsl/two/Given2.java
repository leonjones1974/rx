package com.cam.rxtest.dsl.two;

import rx.Observable;
import rx.functions.Func2;

public interface Given2<T1, T2, U> {

    Given2<T1, T2, U> createSubject(Func2<Observable<T1>, Observable<T2>, Observable<U>> f);

    When2<T1, T2, U> when();

}
