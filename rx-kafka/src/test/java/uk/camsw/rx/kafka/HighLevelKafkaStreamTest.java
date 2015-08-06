package uk.camsw.rx.kafka;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import kafka.message.MessageAndMetadata;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Observable;
import uk.camsw.rx.test.kafka.dsl.KafkaSourceScenario;
import uk.camsw.rx.test.kafka.dsl.StringRenderers;

@Ignore
@RunWith(HierarchicalContextRunner.class)
public class HighLevelKafkaStreamTest {

    private KafkaSourceScenario.Given<String, String, MessageAndMetadata<byte[], byte[]>> given;
    private KafkaSourceScenario.When<String, String, MessageAndMetadata<byte[], byte[]>> when;


    @Before
    public void before() {
        given = new KafkaSourceScenario<String, String, MessageAndMetadata<byte[], byte[]>>().given();
    }

    public class SinglePartition_SingleConsumer {

        @Before
        public void before() {
            when = given
                    .aNewTopic(builder -> builder.withPartitionCount(1))
                    .theStreamUnderTest(topic -> HighLevelKafkaStream.create(topic.getName()))
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

    public class MultiplePartition_SingleConsumer {

        @Before
        public void before() {
            when = given
                    .aNewTopic(builder -> builder.withPartitionCount(2))
                    .theStreamUnderTest(topic -> HighLevelKafkaStream.create(topic.getName()))
                    .theRenderer(StringRenderers::messageAndPartition)
                    .when();
        }

        @Test
        public void subscriberShouldReceiveAllEventsFollowingSubscription() {
            when
                    .theSubscriber().subscribes()
                    .theProducer().produces("0", "a")
                    .theProducer().produces("1", "b")
                    .theSubscriber().waitsForEvents(2)

                    .then()
                    .theSubscribers().renderedStream().containsAllInAnyOrder("[a@part0]-[b@part1]");
        }
    }

    public class MultiplePartitions_MultipleConsumers_DifferentGroups {

        @Before
        public void before() {
            when = given
                    .aNewTopic(builder -> builder.withPartitionCount(2))
                    .theStreamUnderTest(topic -> {
                        Observable<MessageAndMetadata<byte[], byte[]>> stream1 = HighLevelKafkaStream.create(topic.getName(), "some.group");
                        Observable<MessageAndMetadata<byte[], byte[]>> stream2 = HighLevelKafkaStream.create(topic.getName(), "some.other.group");
                        return stream1.mergeWith(stream2);
                    })
                    .theRenderer(StringRenderers::keyAndMessage)
                    .when();
        }

        @Test
        public void bothConsumersShouldEmitAllEvents() {
            when
                    .theSubscriber().subscribes()
                    .theProducer().produces("1", "a")
                    .theProducer().produces("2", "a")
                    .theSubscriber().waitsForEvents(4)

                    .then()
                    .theSubscribers().renderedStream().containsAllInAnyOrder("[1=a]-[1=a]-[2=a]-[2=a]");
        }

    }

    public class MultiplePartitions_MultipleConsumers_SameGroup {

        @Test
        public void eventsShouldBeDistributedAcrossConsumers() {
            new KafkaSourceScenario<String, String, String>().given()
                    .aNewTopic(builder -> builder.withPartitionCount(2))
                    .theStreamUnderTest(topic -> {
                        Observable<String> stream1 = HighLevelKafkaStream.create(topic.getName(), "some.group").map(mamb -> StringRenderers.messageAnd(mamb, "stream1"));
                        Observable<String> stream2 = HighLevelKafkaStream.create(topic.getName(), "some.group").map(mamb -> StringRenderers.messageAnd(mamb, "stream2"));
                        return stream1.mergeWith(stream2);
                    })
                    .when()
                    .theSubscriber().subscribes()
                    .theProducer().produces("0", "a")
                    .theProducer().produces("1", "b")
                    .theSubscriber().waitsForEvents(2)

                    .then()
                    .theSubscribers().renderedStream().containsAllInAnyOrder("[a@stream1]-[b@stream2]");
        }

        @Test
        public void additionalConsumersShouldBeDormant() {
            new KafkaSourceScenario<String, String, String>().given()
                    .aNewTopic(builder -> builder.withPartitionCount(2))
                    .theStreamUnderTest(topic -> {
                        Observable<String> stream1 = HighLevelKafkaStream.create(topic.getName(), "some.group").map(mamb -> StringRenderers.messageAnd(mamb, "stream1"));
                        Observable<String> stream2 = HighLevelKafkaStream.create(topic.getName(), "some.group").map(mamb -> StringRenderers.messageAnd(mamb, "stream2"));
                        Observable<String> stream3 = HighLevelKafkaStream.create(topic.getName(), "some.group").map(mamb -> StringRenderers.messageAnd(mamb, "stream3"));
                        return stream1.mergeWith(stream2).mergeWith(stream3);
                    })
                    .when()
                    .theSubscriber().subscribes()
                    .theProducer().produces("0", "a")
                    .theProducer().produces("1", "b")
                    .theProducer().produces("0", "c")
                    .theProducer().produces("1", "d")
                    .theSubscriber().waitsForEvents(4)

                    .then()
                    .theSubscribers().renderedStream().containsAllInAnyOrder("[a@stream1]-[b@stream2]-[c@stream1]-[d@stream2]");
        }

        @Test
        public void partitionConsumptionShouldFailOverToActiveConsumer() {
            new KafkaSourceScenario<String, String, String>().given()
                    .aNewTopic(builder -> builder.withPartitionCount(2))
                    .theStreamUnderTest(topic -> {
                        Observable<String> stream1 = HighLevelKafkaStream.create(topic.getName(), "some.group").map(mamb -> StringRenderers.messageAnd(mamb, "stream1")).take(1);
                        Observable<String> stream2 = HighLevelKafkaStream.create(topic.getName(), "some.group").map(mamb -> StringRenderers.messageAnd(mamb, "stream2"));
                        return stream1.mergeWith(stream2);
                    })
                    .when()
                    .theSubscriber().subscribes()
                    .theProducer().produces("0", "a")
                    .theProducer().produces("1", "b")
                    .theProducer().produces("0", "c")
                    .theProducer().produces("1", "d")
                    .theSubscriber().waitsForEvents(4)

                    .then()
                    .theSubscribers().renderedStream().containsAllInAnyOrder("[a@stream1]-[b@stream2]-[b@stream2]-[d@stream2]");
        }

        @Test
        public void partitionConsumptionShouldFailOverToPassiveConsumer() {
            new KafkaSourceScenario<String, String, String>()
                    .given()
                    .aNewTopic(builder -> builder.withPartitionCount(2))
                    .theStreamUnderTest(topic -> {
                        Observable<String> stream1 = HighLevelKafkaStream.create(topic.getName(), "some.group").map(mamb -> StringRenderers.messageAnd(mamb, "stream1"));
                        Observable<String> stream2 = HighLevelKafkaStream.create(topic.getName(), "some.group").map(mamb -> StringRenderers.messageAnd(mamb, "stream2")).take(1);
                        Observable<String> stream3 = HighLevelKafkaStream.create(topic.getName(), "some.group").map(mamb -> StringRenderers.messageAnd(mamb, "stream3"));
                        return stream1.mergeWith(stream2).mergeWith(stream3);
                    })
                    .when()
                    .theSubscriber().subscribes()
                    .theProducer().produces("0", "a")
                    .theProducer().produces("1", "b")
                    .theProducer().produces("0", "c")
                    .theProducer().produces("1", "d")
                    .theSubscriber().waitsForEvents(4)

                    .then()
                    .theSubscribers().renderedStream().containsAllInAnyOrder("[a@stream1]-[b@stream2]-[c@stream1]-[d@stream3]");
        }
    }

}