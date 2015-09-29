package uk.camsw.rx.kafka.integration.highlevel;

import kafka.consumer.ConsumerConfig;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import rx.Observable;
import uk.camsw.rx.kafka.HighLevelKafkaStream;
import uk.camsw.rxjava.test.kafka.KafkaEnv;
import uk.camsw.rxjava.test.kafka.dsl.KafkaSourceScenario;
import uk.camsw.rxjava.test.kafka.dsl.StringRenderers;
import uk.camsw.rxjava.test.kafka.rule.EmbeddedKafka;

public class MultiplePartitions_MultipleConsumers_SameGroup {

    @ClassRule
    public static EmbeddedKafka kafka = new EmbeddedKafka();
    private KafkaEnv env;

    @Before
    public void before() {
        env = new KafkaEnv();
    }

    @Test
    public void eventsShouldBeDistributedAcrossConsumers() {
        ConsumerConfig consumerConfig = env.createConsumerConfig(p -> p.setProperty("group.id", "some.group"));
        new KafkaSourceScenario<String, String, String>(env).given()
                .aNewTopic(builder -> builder.withPartitionCount(2))
                .theStreamUnderTest(topic -> {
                    Observable<String> stream1 = HighLevelKafkaStream.create(topic.getName(), consumerConfig).map(mamd -> StringRenderers.messageAnd(mamd, "stream1"));
                    Observable<String> stream2 = HighLevelKafkaStream.create(topic.getName(), consumerConfig).map(mamd -> StringRenderers.messageAnd(mamd, "stream2"));
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
    public void additionalConsumersShouldBePassive() {
        ConsumerConfig consumerConfig = env.createConsumerConfig(p -> p.setProperty("group.id", "some.group"));
        new KafkaSourceScenario<String, String, String>(env).given()
                .aNewTopic(builder -> builder.withPartitionCount(2))
                .theStreamUnderTest(topic -> {
                    Observable<String> stream1 = HighLevelKafkaStream.create(topic.getName(), consumerConfig).map(mamd -> StringRenderers.messageAnd(mamd, "stream1"));
                    Observable<String> stream2 = HighLevelKafkaStream.create(topic.getName(), consumerConfig).map(mamd -> StringRenderers.messageAnd(mamd, "stream2"));
                    Observable<String> stream3 = HighLevelKafkaStream.create(topic.getName(), consumerConfig).map(mamd -> StringRenderers.messageAnd(mamd, "stream3"));
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
        ConsumerConfig consumerConfig = env.createConsumerConfig(p -> p.setProperty("group.id", "some.group"));
        new KafkaSourceScenario<String, String, String>(env).given()
                .aNewTopic(builder -> builder.withPartitionCount(2))
                .theStreamUnderTest(topic -> {
                    Observable<String> stream1 = HighLevelKafkaStream.create(topic.getName(), consumerConfig).map(mamd -> StringRenderers.messageAnd(mamd, "stream1")).take(1);
                    Observable<String> stream2 = HighLevelKafkaStream.create(topic.getName(), consumerConfig).map(mamd -> StringRenderers.messageAnd(mamd, "stream2"));
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
        ConsumerConfig consumerConfig = env.createConsumerConfig(p -> p.setProperty("group.id", "some.group"));
        new KafkaSourceScenario<String, String, String>(env)
                .given()
                .aNewTopic(builder -> builder.withPartitionCount(2))
                .theStreamUnderTest(topic -> {
                    Observable<String> stream1 = HighLevelKafkaStream.create(topic.getName(), consumerConfig).map(mamd -> StringRenderers.messageAnd(mamd, "stream1"));
                    Observable<String> stream2 = HighLevelKafkaStream.create(topic.getName(), consumerConfig).map(mamd -> StringRenderers.messageAnd(mamd, "stream2")).take(1);
                    Observable<String> stream3 = HighLevelKafkaStream.create(topic.getName(), consumerConfig).map(mamd -> StringRenderers.messageAnd(mamd, "stream3"));
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
