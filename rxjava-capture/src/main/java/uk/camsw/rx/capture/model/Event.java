package uk.camsw.rx.capture.model;

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

    public String toString() {
        return value.toString();
    }
}
