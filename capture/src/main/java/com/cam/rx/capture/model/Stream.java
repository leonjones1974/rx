package com.cam.rx.capture.model;

import rx.Observable;
import rx.subjects.ReplaySubject;

public class Stream {

    private final String name;
    private ReplaySubject<Event> events = ReplaySubject.create();

    public Stream(Observable<?> source, String name, boolean isFirst) {
        this.name = name;
        if (source != null) {
            source.subscribe(e -> {
                int eventCount = isFirst
                        ? CaptureModel.instance().nextEventCount()
                        : CaptureModel.instance().eventCount();
                newEvent(new Event(e, eventCount));
            });
        }
    }

    public String getName() {
        return name;
    }

    public Observable<Event> events() {
        return events;
    }

    public void newEvent(Event event) {
       events.onNext(event);
    }

    @Override
    public String toString() {
        return name;
    }
}
