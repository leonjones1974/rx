package uk.camsw.rx.test.dsl.base;

import org.assertj.core.api.AbstractThrowableAssert;

public interface ISubscriber<U, WHEN extends IWhen> {

    WHEN subscribes();

    WHEN unsubscribes();

    WHEN waitsforEvents(int eventCount);

    int eventCount();

    U event(int index);

    int completedCount();

    Class<? extends Throwable> errorClass();

    String errorMessage();

    AbstractThrowableAssert<?, ? extends Throwable> error(int index);

}
