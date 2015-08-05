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