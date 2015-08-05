package uk.camsw.rx.test.kafka.dsl;

import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;
import uk.camsw.rx.test.dsl.given.BaseGiven;
import uk.camsw.rx.test.dsl.scenario.ExecutionContext;
import uk.camsw.rx.test.dsl.when.BaseWhen;
import uk.camsw.rx.test.kafka.Topic;

public class KafkaSourceScenario<K, V, U> {

    private static final String KEY_TOPIC = KafkaSourceScenario.class.getSimpleName() + "_topic";

    private final ExecutionContext<V, ?, U, Given<K, V, U>, When<K, V, U>> context;

    public KafkaSourceScenario() {
        context  = new ExecutionContext<>();
        context.initSteps(new Given<>(context), new When<>(context));
    }

    public Given<K, V, U> given() {
        return new Given<>(context);
    }

    public static class Given<K, V, U> extends BaseGiven<U, Given<K, V, U>, When<K, V, U>> {

        private final ExecutionContext<V, ?, U, Given<K, V, U>, When<K, V, U>> context;

        public Given(ExecutionContext<V, ?, U, Given<K, V, U>, When<K, V, U>> context) {
            super(context);
            this.context = context;
        }

        public Given<K, V, U> theStreamUnderTest(Func1<Topic<K, V>, Observable<U>> f) {
            Observable<U> sut = f.call(context.get(KEY_TOPIC));
            context.setStreamUnderTest(sut);
            return this;
        }


        @Override
        public When<K, V, U> when() {
            return new When<>(context);
        }

        public Given<K, V, U>  theTopic(Func0<Topic<K, V>> f) {
            Topic<K, V> topic = f.call();
            context.put(KEY_TOPIC, topic);
            return this;
        }
    }

    public static class When<K, V, U> extends BaseWhen<U, When<K, V, U>> {

        private final ExecutionContext<V, ?, U, ?, When<K, V, U>> context;

        public When(ExecutionContext<V, ?, U, ?, When<K, V, U>> context) {
            super(context);
            this.context = context;
        }

        public Publisher<K, V, When<K, V, U>> thePublisher() {
            return new Publisher<>(context, context.get(KEY_TOPIC));
        }
    }
}
