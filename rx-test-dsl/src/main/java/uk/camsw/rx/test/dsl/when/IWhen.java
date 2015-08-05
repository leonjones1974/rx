package uk.camsw.rx.test.dsl.when;

import uk.camsw.rx.test.dsl.subscriber.ISubscriber;
import uk.camsw.rx.test.dsl.then.IThen;
import uk.camsw.rx.test.dsl.time.ITime;

public interface IWhen<U, WHEN extends IWhen> {

    ISubscriber<U, WHEN> subscriber(String id);

    ITime<WHEN> time();

    IThen<U> then();

    void go();

}
