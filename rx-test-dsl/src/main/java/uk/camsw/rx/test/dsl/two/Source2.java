package uk.camsw.rx.test.dsl.two;

public interface Source2<T, T1, T2, U> {

    When2<T1, T2, U> emits(T event);

    When2<T1, T2, U> completes();

    When2<T1, T2, U> errors(Throwable t);
}
