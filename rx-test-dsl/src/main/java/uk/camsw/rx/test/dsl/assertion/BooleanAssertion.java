package uk.camsw.rx.test.dsl.assertion;

import org.assertj.core.api.AbstractBooleanAssert;
import uk.camsw.rx.test.dsl.then.IThen;

public class BooleanAssertion<U> extends AbstractBooleanAssert<BooleanAssertion<U>> implements ISubscriberAssertions<U> {

    private final ISubscriberAssertions<U> subscriberAssertions;

    public BooleanAssertion(Boolean actual, ISubscriberAssertions<U> subscriberAssertions) {
        super(actual, BooleanAssertion.class);
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
    public IThen<U> and() {
        return subscriberAssertions.and();
    }

    @Override
    public RenderedStreamAssertion<U> renderedStream() {
        return subscriberAssertions.renderedStream();
    }

    @Override
    public BooleanAssertion<U> isErrored() {
        return subscriberAssertions.isErrored();
    }
}
