package uk.camsw.rx.common;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Pair<T, U> {

    private final T left;
    private final U right;

    public static <T, U> Pair<T, U> of(T left, U right) {
        return new Pair<>(left, right);
    }

    public static <T, U>  Pair<T, U> justLeft(T left) {
        return new Pair<>(left, null);
    }

    public static <T, U>  Pair<T, U> justRight(U right) {
        return new Pair<>(null, right);
    }

    private Pair(T left, U right) {
        this.left = left;
        this.right = right;
    }

    public T left() {
        return left;
    }

    public U right() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair that = (Pair) o;

        return Objects.equal(this.left, that.left) &&
                Objects.equal(this.right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(left, right);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("left", left)
                .add("right", right)
                .toString();
    }
}
