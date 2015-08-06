package uk.camsw.rx.test.kafka.dsl;

import kafka.message.MessageAndMetadata;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import uk.camsw.rx.test.dsl.given.BaseGiven;
import uk.camsw.rx.test.dsl.scenario.ExecutionContext;
import uk.camsw.rx.test.dsl.when.BaseWhen;
import uk.camsw.rx.test.kafka.KafkaEnv;
import uk.camsw.rx.test.kafka.Topic;
import uk.camsw.rx.test.kafka.TopicBuilder;

import java.util.UUID;

/**
 * Create a test scenario where the underlying source is a kafka topic
 *
 * @param <K> The type of the Key used to partition published events
 * @param <V> The type of the Event on the source stream
 * @param <U> The type of the Event emitted by stream under test
 */
public class KafkaSourceScenario<K, V, U> {

    private static final String KEY_TOPIC = KafkaSourceScenario.class.getSimpleName() + "_topic";
    private static final String KEY_ENV = KafkaSourceScenario.class.getSimpleName() + "_env";

    private final ExecutionContext<MessageAndMetadata<byte[], byte[]>, ?, U, Given<K, V, U>, When<K, V, U>> context;

    public KafkaSourceScenario() {
        context = new ExecutionContext<>();
        Given<K, V, U> given = new Given<>(context);
        context.initSteps(given, new When<>(context));
        given.kafkaEnvironment(new KafkaEnv());
    }

    public Given<K, V, U> given() {
        return new Given<>(context);
    }

    public static class Given<K, V, U> extends BaseGiven<U, Given<K, V, U>, When<K, V, U>> {

        private final ExecutionContext<MessageAndMetadata<byte[], byte[]>, ?, U, Given<K, V, U>, When<K, V, U>> context;

        public Given(ExecutionContext<MessageAndMetadata<byte[], byte[]>, ?, U, Given<K, V, U>, When<K, V, U>> context) {
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

        public Given<K, V, U> theTopic(Func0<Topic<K, V>> f) {
            Topic<K, V> topic = f.call();
            context.put(KEY_TOPIC, topic);
            return this;
        }

        public Given<K, V, U> newTopic() {
            return aNewTopic();
        }

        public Given<K, V, U> aNewTopic(Action1<TopicBuilder> config) {
            KafkaEnv env = context.get(KEY_ENV);
            theTopic(() -> {
                String name = "topic-" + UUID.randomUUID().toString();
                TopicBuilder<K, V> builder = TopicBuilder.<K, V>newBuilder(env).forTopic(name);
                if (config != null) config.call(builder);
                Topic<K, V> topic = builder.build();
                context.addFinally(c -> {
                    try {
                        topic.close();
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                });
                return topic;
            });
            return this;
        }

        public Given<K, V, U> aNewTopic() {
            return aNewTopic(null);
        }

        public Given<K, V, U> kafkaEnvironment(KafkaEnv envProperties) {
            context.put(KEY_ENV, envProperties);
            return this;
        }
    }

    public static class When<K, V, U> extends BaseWhen<U, When<K, V, U>> {

        private final ExecutionContext<MessageAndMetadata<byte[], byte[]>, ?, U, Given<K, V, U>, When<K, V, U>> context;

        public When(ExecutionContext<MessageAndMetadata<byte[], byte[]>, ?, U, Given<K, V, U>, When<K, V, U>> context) {
            super(context);
            this.context = context;
        }

        public Producer<K, V, When<K, V, U>> theProducer() {
            return new Producer<>(context, context.get(KEY_TOPIC));
        }
    }
}
