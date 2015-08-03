package uk.camsw.rx.test.kafka;

import com.google.common.base.MoreObjects;

public class Try<T> {

    private final T value;
    private final boolean success;
    private final Throwable error;

    public static <T> Try<T> success(T value) {
        return new Try<>(value);
    }

    public static <T> Try<T> failure(Throwable error) {
        return new Try<>(error);
    }

    private Try(T value) {
        this.error = null;
        this.value = value;
        this.success = true;
    }

    private Try(Throwable e) {
        this.error = e;
        this.value = null;
        this.success = false;
    }

    public T getValue() {
        if (!success) throw new IllegalStateException("Try is not a success", error);
        return value;
    }

    public boolean isSuccess() {
        return success;
    }

    public Throwable getError() {
        if (success) throw new IllegalStateException("Try is not a failure: " + value);
        return error;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("success", success)
                .add("value", value)
                .add("error", error)
                .toString();
    }
}
