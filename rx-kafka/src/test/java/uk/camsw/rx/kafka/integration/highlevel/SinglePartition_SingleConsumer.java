package uk.camsw.rx.kafka.integration.highlevel;

import kafka.consumer.ConsumerConfig;
import kafka.message.MessageAndMetadata;
import org.junit.Before;
import org.junit.Test;
import uk.camsw.rx.kafka.HighLevelKafkaStream;
import uk.camsw.rx.test.kafka.KafkaEnv;
import uk.camsw.rx.test.kafka.dsl.KafkaSourceScenario;
import uk.camsw.rx.test.kafka.dsl.StringRenderers;

public class SinglePartition_SingleConsumer {

    private KafkaSourceScenario.Given<String, String, MessageAndMetadata<byte[], byte[]>> given;
    private KafkaSourceScenario.When<String, String, MessageAndMetadata<byte[], byte[]>> when;
    private KafkaEnv env;
    private ConsumerConfig consumerConfig;

    @Before
    public void before() {
        env = new KafkaEnv();
        consumerConfig = env.createConsumerConfig();

        given = new KafkaSourceScenario<String, String, MessageAndMetadata<byte[], byte[]>>(env).given();
        when = given
                .aNewTopic(builder -> builder.withPartitionCount(1))
                .theStreamUnderTest(topic -> HighLevelKafkaStream.create(topic.getName(), consumerConfig))
                .theRenderer(StringRenderers::keyAndMessage)
                .when();
    }

    @Test
    public void subscriberShouldReceiveAllEventsFollowingSubscription() {
        when
                .theSubscriber().subscribes()
                .theProducer().produces("1", "a")
                .theProducer().produces("1", "b")
                .theSubscriber().waitsForEvents(2)

                .then()
                .theSubscribers().renderedStream().isEqualTo("[1=a]-[1=b]");
    }

    @Test
    public void subscriberShouldNotBeReplayedEventsPublishedPriorToSubscription() {
        when
                .theProducer().produces("1", "a")
                .theSubscriber().subscribes()
                .theProducer().produces("1", "b")
                .theSubscriber().waitsForEvents(1)

                .then()
                .theSubscribers().renderedStream().isEqualTo("[1=b]");
    }

    @Test
    public void subscriberShouldNotReceiveEventsFollowingAnUnsubscribe() {
        when
                .subscriber(1).subscribes()
                .subscriber(2).subscribes()
                .theProducer().produces("1", "a")
                .subscriber(1).waitsForEvents(1)
                .subscriber(1).unsubscribes()
                .theProducer().produces("1", "b")
                .subscriber(2).waitsForEvents(2)
                .then()
                .subscriber(1).renderedStream().isEqualTo("[1=a]")
                .renderedStream().isEqualTo("[1=a]");
    }

    @Test
    public void offsetsShouldFlowFromZero() {
        given.theRenderer(StringRenderers::messageAndOffset);
        when
                .theSubscriber().subscribes()
                .theProducer().produces("1", "a")
                .theProducer().produces("1", "b")
                .theSubscriber().waitsForEvents(2)

                .then()
                .theSubscribers().renderedStream().isEqualTo("[a@offset0]-[b@offset1]");
    }
}
