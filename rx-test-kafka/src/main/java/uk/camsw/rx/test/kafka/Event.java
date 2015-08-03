package uk.camsw.rx.test.kafka;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Event<T> {

    private T value;
    private boolean catchUp;

    public Event(T value, boolean catchUp) {
        this.value = value;
        this.catchUp = catchUp;
    }

    public T getValue() {
        return value;
    }

    public boolean isCatchUp() {
        return catchUp;
    }

    public <R> Event<R> setValue(R value){
        return new Event<R>(value, this.catchUp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event that = (Event) o;

        return Objects.equal(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("catchUp", catchUp)
                .add("value", value)
                .toString();
    }
}
