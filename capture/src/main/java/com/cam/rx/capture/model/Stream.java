package com.cam.rx.capture.model;

import com.google.common.base.Strings;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

public class Stream {

    private final String name;
    private final List<Event> events = new ArrayList<>();


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

    public void newEvent(Event event) {
        System.out.println("event.getOffset() = " + event.getOffset());
        this.events.add(event);
    }

    public List<Event> getEvents() {
        return events;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name + Strings.repeat(" ", 20 - name.length()));
        int eventCount = 0;
        for (Event event : events) {
            int offset = event.getOffset();
            sb.append(Strings.repeat("-", (offset - eventCount)));
            sb.append("O");
            eventCount = offset;
        }
        return sb.toString();
    }
}
