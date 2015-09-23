package uk.camsw.rxjava.test.kafka;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.concurrent.ConcurrentHashMap;

public class SystemPropertyOverrideMap {

    private final ConcurrentHashMap<String, String> inner;

    public SystemPropertyOverrideMap() {
        inner = new ConcurrentHashMap<>();
    }

    public String get(String key) {
        return (System.getProperty(key) != null)
            ? System.getProperty(key)
                : inner.get(key);
    }

    public void put(String key, String value) {
        inner.put(key, value);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SystemPropertyOverrideMap that = (SystemPropertyOverrideMap) o;

        return Objects.equal(this.inner, that.inner);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(inner);
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("inner", inner)
                .toString();
    }
}
