package uk.camsw.rx.kafka.integration.highlevel;

import kafka.consumer.ConsumerConfig;
import kafka.message.MessageAndMetadata;
import org.junit.ClassRule;
import org.junit.Test;
import uk.camsw.rx.kafka.HighLevelKafkaStream;
import uk.camsw.rx.test.kafka.KafkaEnv;
import uk.camsw.rx.test.kafka.dsl.KafkaSourceScenario;
import uk.camsw.rx.test.kafka.dsl.StringRenderers;
import uk.camsw.rx.test.kafka.rule.EmbeddedKafka;

public class MultiplePartitions_SingleConsumer {
    @ClassRule
    public static EmbeddedKafka kafka = new EmbeddedKafka();
    @Test
    public void subscriberShouldReceiveAllEventsFollowingSubscription() {
        KafkaEnv env = new KafkaEnv();
        ConsumerConfig consumerConfig = env.createConsumerConfig();

        new KafkaSourceScenario<String, String, MessageAndMetadata<byte[], byte[]>>(env)
                .given()
                .aNewTopic(builder -> builder.withPartitionCount(2))
                .theStreamUnderTest(topic -> HighLevelKafkaStream.create(topic.getName(), consumerConfig))
                .theRenderer(StringRenderers::messageAndPartition)

                .when()
                .theSubscriber().subscribes()
                .theProducer().produces("0", "a")
                .theProducer().produces("1", "b")
                .theSubscriber().waitsForEvents(2)

                .then()
                .theSubscribers().renderedStream().containsAllInAnyOrder("[a@part0]-[b@part1]");
    }
}
