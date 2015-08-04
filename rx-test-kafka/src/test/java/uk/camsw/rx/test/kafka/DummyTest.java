package uk.camsw.rx.test.kafka;

import org.junit.Ignore;
import org.junit.Test;
import rx.Observable;
import uk.camsw.rx.test.TestScenario;
import uk.camsw.rx.test.kafka.dsl.KafkaScenario;

import java.time.Duration;
import java.util.UUID;

@Ignore("Integration test")
public class DummyTest {

    @Test
    public void exampleKafkaIntegrationTest() {

        String topic = UUID.randomUUID().toString();
        Observable<Event<Try<String>>> kafkaSource = KafkaStream.newBuilder(topic, "group")
                .withConsumerCount(1)
                .newMergedStream();

        TopicBuilder<String, String> topicBuilder = TopicBuilder.<String, String>newBuilder(new EnvProperties()).forTopic(topic);

        KafkaScenario<String, String, String, String> scenario = KafkaScenario.newScenario();

        scenario.given()
                    .theTopic("topic", topicBuilder::build)
                    .asyncTimeout(Duration.ofSeconds(10))
                    .subjectCreated(_source -> kafkaSource.map(e -> e.getValue().getValue()))
                .when()
                    .subscriber("s1").subscribes()
//                    .topic("topic")
//                    .receives("a", "a")
//                    .receives("b", "b")
//                    .and()
                    .subscriber("s1").waitsforEvents(2)
                    .then()
                    .subscriber("s1")
                    .renderedStream().isEqualTo("[a]-[b]");


                        TestScenario.<Event<Try<String>>, String>singleSource()
                                .given()
                                .subjectCreated(_source -> kafkaSource.map(e -> e.getValue().getValue()))
                                .theResource("t1", topicBuilder::build)
                                .asyncTimeout(Duration.ofSeconds(10))
                                .when()
                                .subscriber("s1").subscribes()
                                .<Topic<String, String>>resource("t1")
                                .does(t -> t.publish("a", "a"))
                                .does(t -> t.publish("b", "b"))
                                .and()
                                .subscriber("s1").waitsforEvents(2)
                                .then()
                                .subscriber("s1")
                                .renderedStream().isEqualTo("[a]-[b]");
    }
}