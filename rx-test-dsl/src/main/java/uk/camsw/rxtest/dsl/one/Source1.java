package uk.camsw.rxtest.dsl.one;

public interface Source1<T, T1, U> {

    When1<T1, U> emits(T event);

    When1<T1, U> completes();

    When1<T1, U> errors(Throwable t);

}
