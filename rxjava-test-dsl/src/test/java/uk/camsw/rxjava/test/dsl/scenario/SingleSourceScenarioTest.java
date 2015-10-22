package uk.camsw.rxjava.test.dsl.scenario;

import com.jayway.awaitility.core.ConditionTimeoutException;
import org.junit.Test;
import rx.exceptions.OnErrorNotImplementedException;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import uk.camsw.rxjava.test.dsl.TestScenario;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class SingleSourceScenarioTest {

    @Test
    public void simple() {
        SingleSourceScenario<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .theStreamUnderTest(source -> source.map(s -> Integer.parseInt(s) + 1))

                .when()
                .subscriber("s1").subscribes()
                .theSource().emits("1")
                .theSource().emits("2")
                .theSource().completes()

                .then()
                .subscriber("s1")
                .eventCount().isEqualTo(2)
                .event(0).isEqualTo(2)
                .event(1).isEqualTo(3);
    }

    @Test
    public void multipleSubscribers() {
        SingleSourceScenario<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .theStreamUnderTest(source -> source.map(s -> Integer.parseInt(s) + 1))

                .when()
                .subscriber("s1").subscribes()
                .theSource().emits("1")
                .subscriber("s2").subscribes()
                .theSource().emits("2")

                .then()
                .subscriber("s1")
                .event(0).isEqualTo(2)
                .event(1).isEqualTo(3)
                .eventCount().isEqualTo(2)
                .and()
                .subscriber("s2")
                .event(0).isEqualTo(3)
                .eventCount().isEqualTo(1);
    }

    @Test
    public void unsubscribe() {
        SingleSourceScenario<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .theStreamUnderTest(source -> source.map(s -> Integer.parseInt(s) + 1))

                .when()
                .subscriber("s1").subscribes()
                .theSource().emits("1")
                .subscriber("s1").unsubscribes()
                .theSource().emits("2")

                .then()
                .subscriber("s1")
                .eventCount().isEqualTo(1);
    }

    @Test
    public void completion() {
        SingleSourceScenario<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .theStreamUnderTest(source -> source.map(s -> Integer.parseInt(s) + 1))

                .when()
                .subscriber("s1").subscribes()
                .theSource().emits("1")
                .theSource().completes()

                .then()
                .subscriber("s1")
                .isErrored().isFalse()
                .completedCount().isEqualTo(1);
    }

    @Test
    public void handledError() {
        SingleSourceScenario<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .theStreamUnderTest(source -> source.map(s -> Integer.parseInt(s) + 1))
                .errorsAreHandled()

                .when()
                .subscriber("s1").subscribes()
                .theSource().emits("1")
                .theSource().errors(new IllegalArgumentException("oh no"))

                .then()
                .subscriber("s1")
                .isErrored().isTrue()
                .errorClass().isEqualTo(IllegalArgumentException.class)
                .errorMessage().isEqualTo("oh no");
    }

    @Test(expected = OnErrorNotImplementedException.class)
    public void unhandledError() {
        SingleSourceScenario<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .theStreamUnderTest(source -> source.map(s -> Integer.parseInt(s) + 1))

                .when()
                .subscriber("s1").subscribes()
                .theSource().emits("1")
                .theSource().errors(new IllegalArgumentException("oh no"))

                .go();
    }

    @Test
    public void temporal() {
        SingleSourceScenario<String, List<String>> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .theStreamUnderTest((source, scheduler) -> source.buffer(10, TimeUnit.SECONDS, scheduler))

                .when()
                .subscriber("s1").subscribes()
                .theSource().emits("1a")
                .theSource().emits("1b")
                .theSource().emits("1c")
                .time().advancesBy(Duration.ofSeconds(11))
                .theSource().emits("2a")
                .theSource().emits("2b")
                .theSource().completes()

                .then()
                .subscriber("s1")
                .eventCount().isEqualTo(2)
                .event(0).isEqualTo(asList("1a", "1b", "1c"))
                .event(1).isEqualTo(asList("2a", "2b"));
    }


    @Test
    public void manualSingleSource() {
        PublishSubject<String> customSource = PublishSubject.create();
        TestScenario.<String, String>singleSource()
                .given()
                .theCustomSource(customSource)
                .theStreamUnderTest(() -> customSource.map(String::toUpperCase))

                .when()
                .subscriber("s1").subscribes()
                .theSource().emits("a")
                .theSource().emits("b")

                .then()
                .subscriber("s1")
                .eventCount().isEqualTo(2)
                .event(0).isEqualTo("A")
                .event(1).isEqualTo("B");

    }

    @Test
    public void streamRendering() {
        SingleSourceScenario<Integer, String> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .theStreamUnderTest(source -> source.map(n -> n == 0 ? "a" : "B"))
                .theRenderer(event -> "'" + event + "'")

                .when()
                .subscriber("s1").subscribes()
                .theSource().emits(0)
                .theSource().emits(1)
                .theSource().completes()

                .then()
                .subscriber("s1")
                .eventCount().isEqualTo(2)
                .renderedStream().isEqualTo("['a']-['B']-|")
                .completedCount().isEqualTo(1)
        ;
    }

    @Test
    public void streamRenderingWithError() {
        SingleSourceScenario<Integer, String> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .theStreamUnderTest(source -> source.map(n -> n == 0 ? "a" : "B"))
                .errorsAreHandled()
                .theRenderer(event -> "'" + event + "'")

                .when()
                .subscriber("s1").subscribes()
                .theSource().emits(0)
                .theSource().emits(1)
                .theSource().errors(new RuntimeException("I'm broken"))

                .then()
                .subscriber("s1")
                .renderedStream().isEqualTo("['a']-['B']-X[RuntimeException: I'm broken]")
                .isErrored().isTrue()
                .eventCount().isEqualTo(2);

    }

    @Test
    public void asyncWaitForEvents() {
        SingleSourceScenario<String, String> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .theStreamUnderTest(source -> source.observeOn(Schedulers.computation()).delay(1, TimeUnit.SECONDS))
                .asyncTimeoutOf(Duration.ofSeconds(2))

                .when()
                .subscriber("s1").subscribes()
                .theSource().emits("a")
                .theSource().emits("b")
                .subscriber("s1").waitsForEvents(2)

                .then()
                .subscriber("s1")
                .renderedStream().isEqualTo("[a]-[b]")
                .eventCount().isEqualTo(2);
    }

    @Test(expected = ConditionTimeoutException.class)
    public void asyncWithTimeout() {
        SingleSourceScenario<String, String> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .theStreamUnderTest(source -> source.observeOn(Schedulers.computation()).delay(10, TimeUnit.SECONDS))
                .asyncTimeoutOf(Duration.ofMillis(500))

                .when()
                .subscriber("s1").subscribes()
                .theSource().emits("a")
                .theSource().emits("b")
                .subscriber("s1").waitsForEvents(2)

                .go();
    }

    @Test
    public void customActions() {
        SingleSourceScenario<String, String> testScenario = TestScenario.singleSource();
        AtomicBoolean s1 = new AtomicBoolean(false);
        testScenario
                .given()

                .when()
                .actionIsPerformed(() -> s1.set(true))

                .go();

        assertThat(s1.get()).isTrue();
    }


}