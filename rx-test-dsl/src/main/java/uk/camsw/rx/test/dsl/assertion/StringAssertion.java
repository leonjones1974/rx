package uk.camsw.rx.test.dsl.assertion;

import org.assertj.core.api.AbstractCharSequenceAssert;
import uk.camsw.rx.test.dsl.impl.Then;

public class StringAssertion<U> extends AbstractCharSequenceAssert<StringAssertion<U>, CharSequence> implements ISubscriberAssertions<U> {

    private final ISubscriberAssertions<U> subscriberAssertions;

    public StringAssertion(String value, ISubscriberAssertions<U> subscriberAssertions) {
        super(value, StringAssertion.class);
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
    public Then<U> and() {
        return subscriberAssertions.and();
    }

    @Override
    public BooleanAssertion<U> isErrored() {
        return subscriberAssertions.isErrored();
    }

    @Override
    public StringAssertion<U> renderedStream() {
        return subscriberAssertions.renderedStream();
    }
}