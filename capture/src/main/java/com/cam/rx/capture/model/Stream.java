package com.cam.rx.capture.model;

import com.google.common.base.Strings;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Stream {

    private final String name;
    private final List<Event> events = new ArrayList<>();

    public Stream(String name) {
        this.name = name;
    }

    public void newEvent(Event event) {
        System.out.println("[" + name +"]: " + event);
        this.events.add(event);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(name + Strings.repeat(" ", 20 - name.length()));
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
