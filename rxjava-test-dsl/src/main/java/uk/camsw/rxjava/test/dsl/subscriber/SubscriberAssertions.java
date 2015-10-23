package uk.camsw.rxjava.test.dsl.subscriber;

import com.google.common.base.Joiner;
import rx.functions.Func1;
import uk.camsw.rxjava.test.dsl.assertion.*;
import uk.camsw.rxjava.test.dsl.scenario.ExecutionContext;
import uk.camsw.rxjava.test.dsl.then.BaseThen;
import uk.camsw.rxjava.test.dsl.when.IWhen;

import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SubscriberAssertions<U> implements ISubscriberAssertions<U> {

    private final ExecutionContext<?, ?, U, ?, ?> context;
    private final ISubscriber<U, ? extends IWhen> testSubscriber;

    public SubscriberAssertions(ExecutionContext<?, ?, U, ?, ?> context, ISubscriber<U, ? extends IWhen> testSubscriber) {
        this.context = context;
        this.testSubscriber = testSubscriber;
    }

    @Override
    public ObjectAssertion<U> event(int index) {
        return new ObjectAssertion<>(testSubscriber.event(index), this);
    }

    @Override
    public IntegerAssertion<U> eventCount() {
        return new IntegerAssertion<>(testSubscriber.eventCount(), this);
    }

    @Override
    public IntegerAssertion<U> completedCount() {
        return new IntegerAssertion<>(testSubscriber.completedCount(), this);
    }

    @Override
    public ClassAssertion<U> errorClass() {
        return new ClassAssertion<>(testSubscriber.errorClass(), this);
    }

    @Override
    public StringAssertion<U> errorMessage() {
        return new StringAssertion<>(testSubscriber.errorMessage(), this);
    }

    @Override
    public BaseThen<U> and() {
        return new BaseThen<>(context);
    }

    @Override
    public RenderedStreamAssertion<U> renderedStream() {
        StringBuilder rendering = new StringBuilder(Joiner.on('-').join(
                testSubscriber.events().stream().map(e -> {
                    Func1<U, String> renderer = context.getRenderer();
                    return "[" + renderer.call(e) + "]";
                }).collect(Collectors.toList())));

        for (int i = 0; i < testSubscriber.completedCount(); i++) rendering.append("-|");

        if (testSubscriber.isErrored())
            rendering.append("-X[").append(testSubscriber.errorClass().getSimpleName()).append(": ").append(testSubscriber.errorMessage()).append("]");
        return new RenderedStreamAssertion<>(rendering.toString(), this);
    }

    @Override
    public ISubscriberAssertions<U> eventsMatch(Predicate<? super U> p) {
        testSubscriber.events()
                .forEach(e -> new ObjectAssertion<>(e, this).matches(p));
        return this;
    }

    @Override
    public ISubscriberAssertions<U> eventsMatch(Predicate<? super U> p, String description) {
        testSubscriber.events()
                .forEach(e -> new ObjectAssertion<>(e, this).matches(p, description));
        return this;
    }

    @Override
    public BooleanAssertion<U> isErrored() {
        return new BooleanAssertion<>(testSubscriber.isErrored(), this);
    }
}
