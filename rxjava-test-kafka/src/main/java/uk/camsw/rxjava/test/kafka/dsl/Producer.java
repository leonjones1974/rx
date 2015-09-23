package uk.camsw.rxjava.test.kafka.dsl;

import kafka.message.MessageAndMetadata;
import uk.camsw.rxjava.test.dsl.scenario.ExecutionContext;
import uk.camsw.rxjava.test.dsl.when.IWhen;
import uk.camsw.rxjava.test.kafka.Topic;

public class Producer<K, V, WHEN extends IWhen> {

    private final ExecutionContext<MessageAndMetadata<byte[], byte[]>, ?, ?, ?, WHEN> context;
    private final Topic<K, V> topic;

    public Producer(ExecutionContext<MessageAndMetadata<byte[], byte[]>, ?, ?, ?, WHEN> context, Topic<K, V> topic) {
        this.context = context;
        this.topic = topic;
    }

    public WHEN produces(K k, V v) {
        context.addCommand(c -> topic.publish(k, v));
        return context.getWhen();
    }

}
