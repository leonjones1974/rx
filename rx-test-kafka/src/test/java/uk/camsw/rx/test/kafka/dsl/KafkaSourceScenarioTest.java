package uk.camsw.rx.test.kafka.dsl;

import org.junit.Ignore;
import org.junit.Test;
import uk.camsw.rx.test.kafka.KafkaStream;

import java.time.Duration;
import java.util.UUID;

@Ignore("Manual Integration")
public class KafkaSourceScenarioTest {

    @Test
    public void exampleKafkaIntegrationTest() {
        String group = UUID.randomUUID().toString();

        new KafkaSourceScenario<String, String, String>()
                .given()
                    .aNewTopic()
                    .asyncTimeoutOf(Duration.ofSeconds(10))
                    .theStreamUnderTest(topic -> KafkaStream.newBuilder(topic.getName(), group).newMergedStream().map(e -> e.getValue().getValue()))
                .when()
                    .theSubscriber().subscribes()
                    .theProducer().produces("1", "1")
                    .theProducer().produces("2", "2")
                    .theSubscriber().waitsForEvents(2)
                .then()
                    .theSubscribers().renderedStream().isEqualTo("[1]-[2]");

    }
}