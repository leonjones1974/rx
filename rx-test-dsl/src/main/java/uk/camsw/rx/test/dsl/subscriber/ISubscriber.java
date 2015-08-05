package uk.camsw.rx.test.dsl.subscriber;

import org.assertj.core.api.AbstractThrowableAssert;
import uk.camsw.rx.test.dsl.when.IWhen;

public interface ISubscriber<U, WHEN extends IWhen> {

    WHEN subscribes();

    WHEN unsubscribes();

    WHEN waitsForEvents(int eventCount);

    int eventCount();

    U event(int index);

    int completedCount();

    Class<? extends Throwable> errorClass();

    String errorMessage();

    AbstractThrowableAssert<?, ? extends Throwable> error(int index);

}
