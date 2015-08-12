package uk.camsw.rx.kafka.integration.highlevel;

import kafka.consumer.ConsumerConfig;
import kafka.message.MessageAndMetadata;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;
import uk.camsw.rx.kafka.HighLevelKafkaStream;
import uk.camsw.rx.test.kafka.KafkaEnv;
import uk.camsw.rx.test.kafka.dsl.KafkaSourceScenario;
import uk.camsw.rx.test.kafka.dsl.StringRenderers;
import uk.camsw.rx.test.kafka.rule.EmbeddedKafka;

public class MultiplePartitions_MultipleConsumers_DifferentGroups {

    @ClassRule
    public static EmbeddedKafka kafka = new EmbeddedKafka();

    @Test
    public void bothConsumersShouldEmitAllEvents() {
        KafkaEnv env = new KafkaEnv();
        ConsumerConfig consumerConfig1 = env.createConsumerConfig(p -> p.setProperty("group.id", "some.group"));
        ConsumerConfig consumerConfig2 = env.createConsumerConfig(p -> p.setProperty("group.id", "some.other.group"));
        new KafkaSourceScenario<String, String, MessageAndMetadata<byte[], byte[]>>(env)
                .given()
                .aNewTopic(builder -> builder.withPartitionCount(2))
                .theStreamUnderTest(topic -> {
                    Observable<MessageAndMetadata<byte[], byte[]>> stream1 = HighLevelKafkaStream.create(topic.getName(), consumerConfig1);
                    Observable<MessageAndMetadata<byte[], byte[]>> stream2 = HighLevelKafkaStream.create(topic.getName(), consumerConfig2);
                    return stream1.mergeWith(stream2);
                })
                .theRenderer(StringRenderers::keyAndMessage)

                .when()
                .theSubscriber().subscribes()
                .theProducer().produces("1", "a")
                .theProducer().produces("2", "a")
                .theSubscriber().waitsForEvents(4)

                .then()
                .theSubscribers().renderedStream().containsAllInAnyOrder("[1=a]-[1=a]-[2=a]-[2=a]");
    }
}
