package uk.camsw.rx.test.kafka.dsl;

import org.junit.Test;
import uk.camsw.rx.test.kafka.EnvProperties;
import uk.camsw.rx.test.kafka.KafkaStream;
import uk.camsw.rx.test.kafka.TopicBuilder;

import java.time.Duration;
import java.util.UUID;

public class KafkaSourceScenarioTest {

    @Test
    public void exampleKafkaIntegrationTest() {
        String topic = UUID.randomUUID().toString();
        String group = UUID.randomUUID().toString();

        TopicBuilder<String, String> topicBuilder = TopicBuilder.<String, String>newBuilder(new EnvProperties()).forTopic(topic);

        new KafkaSourceScenario<String, String, String>()
                .given()
                    .theTopic(topicBuilder::build)
                    .asyncTimeout(Duration.ofSeconds(10))
                    .theStreamUnderTest(t -> KafkaStream.newBuilder(t.getName(), group).newMergedStream().map(e -> e.getValue().getValue()))
                .when()
                    .theSubscriber().subscribes()
                    .thePublisher().publishes("1", "1")
                    .thePublisher().publishes("2", "2")
                    .theSubscriber().waitsForEvents(2)
                .then()
                    .theSubscribers().renderedStream().isEqualTo("[1]-[2]");

    }
}