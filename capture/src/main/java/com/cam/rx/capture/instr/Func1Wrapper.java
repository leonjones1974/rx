package com.cam.rx.capture.instr;

import rx.functions.Func1;

public class Func1Wrapper<T, R> implements Func1<T, R> {
    private final Func1<T, R> inner;

    public Func1Wrapper(Func1<T, R> inner) {
        this.inner = inner;
    }

    @Override
    public R call(T t) {
        System.out.println("Wrapped IN: " + t);
        R r = inner.call(t);
        System.out.println("Wrapped OUT: " + r);
        return r;
    }
}
