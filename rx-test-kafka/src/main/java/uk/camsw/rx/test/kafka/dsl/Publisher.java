package uk.camsw.rx.test.kafka.dsl;

import uk.camsw.rx.test.dsl.scenario.ExecutionContext;
import uk.camsw.rx.test.dsl.when.IWhen;
import uk.camsw.rx.test.kafka.Topic;

public class Publisher<K, V, WHEN extends IWhen> {

    private final ExecutionContext<V, ?, ?, ?, WHEN> context;
    private final Topic<K, V> topic;

    public Publisher(ExecutionContext<V, ?, ?, ?, WHEN> context, Topic<K, V> topic) {
        this.context = context;
        this.topic = topic;
    }

    public WHEN publishes(K k, V v) {
        context.addCommand(c -> topic.publish(k, v));
        return context.getWhen();
    }

}
