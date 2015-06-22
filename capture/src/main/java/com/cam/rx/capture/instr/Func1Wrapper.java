package com.cam.rx.capture.instr;

import com.cam.rx.capture.model.CaptureModel;
import com.cam.rx.capture.model.Event;
import com.cam.rx.capture.model.Stream;
import rx.functions.Func1;

public class Func1Wrapper<T, R> implements Func1<T, R> {
    private final Func1<T, R> inner;
    private final Stream stream;

    public Func1Wrapper(Func1<T, R> inner, Stream stream) {
        this.inner = inner;
        this.stream = stream;
    }

    @Override
    public R call(T t) {
        R r = inner.call(t);
        stream.newEvent(new Event(t, CaptureModel.instance().nextEventCount()));
        return r;
    }
}
