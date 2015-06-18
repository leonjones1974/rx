package com.cam.rxtest;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.functions.Func2;

public interface Given1<T1, U> {

    Given1<T1, U> createSubject(Func1<Observable<T1>, Observable<U>> f);

    Given1<T1, U> createSubjectWithScheduler(Func2<Observable<T1>, Scheduler, Observable<U>> f);

    When1<T1, U> when();
}
