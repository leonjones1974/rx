package uk.camsw.rxjava.test.dsl.subscriber;

import org.assertj.core.api.AbstractThrowableAssert;
import uk.camsw.rxjava.test.dsl.when.IWhen;

import java.util.List;

public interface ISubscriber<U, WHEN extends IWhen> {

    WHEN subscribes();

    WHEN unsubscribes();

    // todo: waitsFor(2).events(), is nicer as can then factor in time if need to
    WHEN waitsForEvents(int eventCount);

    int eventCount();

    U event(int index);

    List<U> events();

    int completedCount();

    boolean isErrored();

    Class<? extends Throwable> errorClass();

    String errorMessage();

    AbstractThrowableAssert<?, ? extends Throwable> error(int index);

}
