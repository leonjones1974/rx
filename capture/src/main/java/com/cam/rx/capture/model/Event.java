package com.cam.rx.capture.model;

import com.google.common.base.MoreObjects;

public class Event {
    private final Object value;
    private final int offset;

    public Event(Object value, int offset) {
        this.value = value;
        this.offset = offset;
    }

    public Object getValue() {
        return value;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("value", value)
                .add("offset", offset)
                .toString();
    }
}
