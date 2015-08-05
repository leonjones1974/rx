package uk.camsw.rx.test.dsl.assertion;

import org.assertj.core.api.AbstractObjectAssert;
import uk.camsw.rx.test.dsl.base.IThen;

public class ObjectAssertion<U> extends AbstractObjectAssert<ObjectAssertion<U>, U> implements ISubscriberAssertions<U> {
    private final ISubscriberAssertions<U> subscriberAssertions;

    public ObjectAssertion(U actual, ISubscriberAssertions<U> subscriberAssertions) {
        super(actual, ObjectAssertion.class);
        this.subscriberAssertions = subscriberAssertions;
    }

    @Override
    public ObjectAssertion<U> event(int index) {
        return subscriberAssertions.event(index);
    }

    @Override
    public IntegerAssertion<U> eventCount() {
        return subscriberAssertions.eventCount();
    }

    @Override
    public IntegerAssertion<U> completedCount() {
        return subscriberAssertions.completedCount();
    }

    @Override
    public ClassAssertion<U> errorClass() {
        return subscriberAssertions.errorClass();
    }

    @Override
    public StringAssertion<U> errorMessage() {
        return subscriberAssertions.errorMessage();
    }

    @Override
    public BooleanAssertion<U> isErrored() {
        return subscriberAssertions.isErrored();
    }

    @Override
    public IThen<U> and() {
        return subscriberAssertions.and();
    }

    @Override
    public StringAssertion<U> renderedStream() {
        return subscriberAssertions.renderedStream();
    }
}
