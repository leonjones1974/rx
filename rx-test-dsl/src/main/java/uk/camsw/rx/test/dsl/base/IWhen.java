package uk.camsw.rx.test.dsl.base;

public interface IWhen<U, WHEN extends IWhen> {

    ISubscriber<U, WHEN> subscriber(String id);

    ITime<WHEN> time();

    IThen<U> then();

    void go();

}
