package uk.camsw.rx.test.kafka;

import org.junit.Ignore;
import org.junit.Test;
import rx.Observable;
import uk.camsw.rx.test.TestScenario;

@Ignore("Integration test")
public class DummyTest {

    @Test
    public void itShould_CreateIt() {

        TestScenario.singleSource()
                .given()
                .theResource(() -> TopicBuilder.newBuilder(new EnvProperties()).build())
                .createSubject(_source -> Observable.just(1, 2, 3, 4))
                .when()
                .subscriber("s1").subscribes()
                .then()
                .subscriber("s1")
                .eventCount().isEqualTo(4);

    }

}