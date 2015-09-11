package uk.camsw.rx.common;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PairTest {

    private Pair<String, String> subject;

    @Test
    public void itShould_SupportLeftOnly() {
        subject = Pair.justLeft("a");

        assertThat(subject.left()).isEqualTo("a");
        assertThat(subject.right()).isNull();
    }

    @Test
    public void itShould_SupportRightOnly() {
        subject = Pair.justRight("b");

        assertThat(subject.right()).isEqualTo("b");
        assertThat(subject.left()).isNull();
    }

    @Test
    public void itShould_SupportBothLeftAndRight() {
        subject = Pair.of("a", "b");

        assertThat(subject.left()).isEqualTo("a");
        assertThat(subject.right()).isEqualTo("b");
    }

    @Test
    public void itShould_ImplementEquals() {
        assertThat(Pair.of("a", "b")).isEqualTo(Pair.of("a", "b"));
        assertThat(Pair.of("a", "b")).isNotEqualTo(Pair.of("b", "a"));
        assertThat(Pair.of(null, null)).isEqualTo(Pair.of(null, null));
    }

}