package uk.camsw.rx.test.kafka;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SystemPropertyOverrideMapTest {

    public static final String KEY = "some.property";
    public static final String VALUE = "some.value";
    public static final String OVERRIDE_VALUE = "some.override.value";
    private SystemPropertyOverrideMap subject;

    @Before
    public void before() {
        subject = new SystemPropertyOverrideMap();
    }

    @Test
    public void itShould_ReturnNullWherePropertyNotSetOrOverridden() {
        assertThat(subject.get(KEY)).isNull();
    }

    @Test
    public void itShould_ReturnTheValueWhereSetAndNotOverridden() {
        subject.put(KEY, VALUE);
        assertThat(subject.get(KEY)).isEqualTo(VALUE);
    }

    @Test
    public void itShould_ReturnTheOverrideValueWherePresentAndNotSet() {
        try {
            System.setProperty(KEY, OVERRIDE_VALUE);
            assertThat(subject.get(KEY)).isEqualTo(OVERRIDE_VALUE);
        } finally {
            System.clearProperty(KEY);
        }
    }

    @Test
    public void itShould_ReturnTheOverrideValueWherePresentAndSet() {
        try {
            subject.put(KEY, VALUE);
            System.setProperty(KEY, OVERRIDE_VALUE);
            assertThat(subject.get(KEY)).isEqualTo(OVERRIDE_VALUE);
        } finally {
            System.clearProperty(KEY);
        }
    }

}