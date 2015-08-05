package uk.camsw.rx.test.dsl.base;

import org.assertj.core.api.AbstractThrowableAssert;
import rx.exceptions.OnErrorNotImplementedException;
import rx.subscriptions.SerialSubscription;
import uk.camsw.rx.test.dsl.impl.ExecutionContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseSubscriber<U, WHEN extends IWhen> implements ISubscriber<U, WHEN>, rx.Observer<U> {
    private final SerialSubscription subscription = new SerialSubscription();

    private final String id;
    private final ExecutionContext<?, ?, U, ?, WHEN> context;
    private final rx.observers.TestSubscriber<U> inner;

    public BaseSubscriber(String id, ExecutionContext<?, ?, U, ?, WHEN> context) {
        this.id = id;
        this.context= context;
        this.inner = new rx.observers.TestSubscriber<>();
    }

    public String getId() {
        return id;
    }

    @Override
    public WHEN subscribes() {
        context.addCommand(c -> subscription.set(c.getStreamUnderTest().subscribe(this)));
        return context.getWhen();
    }

    @Override
    public WHEN unsubscribes() {
        context.addCommand(c -> subscription.unsubscribe());
        return context.getWhen();
    }

    @Override
    public WHEN waitsforEvents(int eventCount) {
        context.addCommand(context -> context.await().until(() -> inner.getOnNextEvents().size() >= eventCount));
        return context.getWhen();
    }

    @Override
    public int eventCount() {
        return inner.getOnNextEvents().size();
    }

    public boolean isErrored() {
        return inner.getOnErrorEvents().size() > 0;
    }

    @Override
    public U event(int index) {
        return getEvent(index);
    }

    public List<U> events() {
        return inner.getOnNextEvents();
    }

    private U getEvent(int index) {
        return inner.getOnNextEvents().get(index);
    }

    @Override
    public int completedCount() {
        return inner.getOnCompletedEvents().size();
    }

    @Override
    public Class<? extends Throwable> errorClass() {
        //noinspection ThrowableResultOfMethodCallIgnored
        return inner.getOnErrorEvents().get(0).getClass();
    }

    @Override
    public String errorMessage() {
        return inner.getOnErrorEvents().get(0).getMessage();
    }

    @Override
    public AbstractThrowableAssert<?, ? extends Throwable> error(int index) {
        return assertThat(inner.getOnErrorEvents().get(index));
    }

    @Override
    public void onCompleted() {
        inner.onCompleted();
    }

    @Override
    public void onError(Throwable e) {
        if (context.handleErrors()) {
            inner.onError(e);
        } else {
            context.cleanUp();
            throw new OnErrorNotImplementedException("Unhandled Error", e);
        }
    }

    @Override
    public void onNext(U next) {
        inner.onNext(next);
    }
}
