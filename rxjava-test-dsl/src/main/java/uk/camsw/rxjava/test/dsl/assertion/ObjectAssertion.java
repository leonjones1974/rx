package uk.camsw.rxjava.test.dsl.assertion;

import org.assertj.core.api.AbstractObjectAssert;
import uk.camsw.rxjava.test.dsl.then.IThen;

import java.util.function.Predicate;

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
    public RenderedStreamAssertion<U> renderedStream() {
        return subscriberAssertions.renderedStream();
    }

    @Override
    public ISubscriberAssertions<U> allEventsMatch(Predicate<? super U> p) {
        subscriberAssertions.allEventsMatch(p);
        return this;
    }

    @Override
    public ISubscriberAssertions<U> allEventsMatch(Predicate<? super U> p, String description) {
        subscriberAssertions.allEventsMatch(p, description);
        return this;
    }

    @Override
    public ISubscriberAssertions<U> atLeastOneEventMatches(Predicate<? super U> p) {
        subscriberAssertions.atLeastOneEventMatches(p);
        return this;
    }

    @Override
    public ISubscriberAssertions<U> atLeastOneEventMatches(Predicate<? super U> p, String description) {
        subscriberAssertions.atLeastOneEventMatches(p, description);
        return this;
    }
}
