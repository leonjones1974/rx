package uk.camsw.rxjava.test.dsl.assertion;

import org.assertj.core.api.AbstractIntegerAssert;
import uk.camsw.rxjava.test.dsl.then.IThen;

import java.util.function.Predicate;

public class IntegerAssertion<U> extends AbstractIntegerAssert<IntegerAssertion<U>> implements ISubscriberAssertions<U> {

    private final ISubscriberAssertions<U> subscriberAssertions;

    public IntegerAssertion(int value, ISubscriberAssertions<U> subscriberAssertions) {
        super(value, IntegerAssertion.class);
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
    public IThen<U> and() {
        return subscriberAssertions.and();
    }

    @Override
    public RenderedStreamAssertion<U> renderedStream() {
        return subscriberAssertions.renderedStream();
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
    public ISubscriberAssertions<U> receivedOnlyEventsMatching(Predicate<U> p) {
        subscriberAssertions.receivedOnlyEventsMatching(p);
        return this;
    }

    @Override
    public ISubscriberAssertions<U> receivedOnlyEventsMatching(Predicate<U> p, String description) {
        subscriberAssertions.receivedOnlyEventsMatching(p, description);
        return this;
    }

    @Override
    public ISubscriberAssertions<U> receivedAtLeastOneMatch(Predicate<U> p) {
        subscriberAssertions.receivedAtLeastOneMatch(p);
        return this;
    }

    @Override
    public ISubscriberAssertions<U> receivedAtLeastOneMatch(Predicate<U> p, String description) {
        subscriberAssertions.receivedAtLeastOneMatch(p, description);
        return this;
    }

}
