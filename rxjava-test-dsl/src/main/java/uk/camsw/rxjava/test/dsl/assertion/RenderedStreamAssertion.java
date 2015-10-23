package uk.camsw.rxjava.test.dsl.assertion;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.assertj.core.api.AbstractCharSequenceAssert;
import uk.camsw.rxjava.test.dsl.then.IThen;

import java.util.ArrayList;
import java.util.function.Predicate;

public class RenderedStreamAssertion<U> extends AbstractCharSequenceAssert<RenderedStreamAssertion<U>, CharSequence> implements ISubscriberAssertions<U> {

    private final ISubscriberAssertions<U> subscriberAssertions;

    public RenderedStreamAssertion(String value, ISubscriberAssertions<U> subscriberAssertions) {
        super(value, RenderedStreamAssertion.class);
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

    public RenderedStreamAssertion<U> containsAllInAnyOrder(String expected) {
        ArrayList<String> actualItems = Lists.newArrayList(Splitter.on('-').split(actual));
        ArrayList<String> expectedItems = Lists.newArrayList(Splitter.on('-').split(expected));
        if (actualItems.size() == expectedItems.size()
                && actualItems.containsAll(expectedItems)) return this;

        failWithMessage(actual + " does not contain all of " + expected);
        return this;
    }

}
