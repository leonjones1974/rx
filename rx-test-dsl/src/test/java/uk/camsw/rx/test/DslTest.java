package uk.camsw.rx.test;

import com.jayway.awaitility.core.ConditionTimeoutException;
import rx.exceptions.OnErrorNotImplementedException;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import uk.camsw.rx.test.dsl.one.Scenario1;
import uk.camsw.rx.test.dsl.two.Scenario2;
import org.junit.Test;
import rx.Observable;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class DslTest {

    @Test
    public void simple() {
        Scenario1<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .subjectCreated(source -> source.map(s -> Integer.parseInt(s) + 1))
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
        Scenario1<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .subjectCreated(source -> source.map(s -> Integer.parseInt(s) + 1))
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
        Scenario1<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .subjectCreated(source -> source.map(s -> Integer.parseInt(s) + 1))
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
        Scenario1<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .subjectCreated(source -> source.map(s -> Integer.parseInt(s) + 1))
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
        Scenario1<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .subjectCreated(source -> source.map(s -> Integer.parseInt(s) + 1))
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
        Scenario1<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .subjectCreated(source -> source.map(s -> Integer.parseInt(s) + 1))
                .when()
                .subscriber("s1").subscribes()
                .theSource().emits("1")
                .theSource().errors(new IllegalArgumentException("oh no"))
                .go();
    }

    @Test
    public void temporal() {
        Scenario1<String, List<String>> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .subjectCreatedWithScheduler((source, scheduler) -> source.buffer(10, TimeUnit.SECONDS, scheduler))
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
    public void merge() {
        Scenario2<String, String, String> testScenario = TestScenario.twoSources();

        testScenario
                .given()
                .subjectCreated(Observable::mergeWith)
                .when()
                .subscriber("s1").subscribes()
                .source1().emits("1")
                .source2().emits("a")
                .source1().emits("2")
                .source2().emits("b")
                .source1().completes()
                .source2().completes()
                .then()
                .subscriber("s1")
                .eventCount().isEqualTo(4)
                .event(0).isEqualTo("1")
                .event(1).isEqualTo("a")
                .event(2).isEqualTo("2")
                .event(3).isEqualTo("b");
    }

    @Test
    public void zip() {
        Scenario2<String, Integer, String> testScenario = TestScenario.twoSources();

        testScenario
                .given()
                .subjectCreated((s1, s2) -> s1.zipWith(s2, (z, n) -> z + n))
                .renderer(s -> s)
                .when()
                .subscriber("s1").subscribes()
                .source1().emits("a")
                .source2().emits(1)
                .source1().emits("b")
                .source2().emits(2)
                .source1().completes()
                .source2().completes()
                .then()
                .subscriber("s1")
                .eventCount().isEqualTo(2)
                .renderedStream().isEqualTo("[a1]-[b2]-|");
    }

    @Test
    public void manualSingleSource() {
        PublishSubject<String> customSource = PublishSubject.create();
        TestScenario.singleSource(customSource)
                .given()
                .subjectCreated(_source -> customSource.map(String::toUpperCase))
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
        Scenario1<Integer, String> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .subjectCreated(source -> source.map(n -> n == 0 ? "a" : "B"))
                .renderer(event -> "'" + event + "'")
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
        Scenario1<Integer, String> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .subjectCreated(source -> source.map(n -> n == 0 ? "a" : "B"))
                .errorsAreHandled()
                .renderer(event -> "'" + event + "'")
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
        Scenario1<String, String> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .subjectCreated(source -> source.observeOn(Schedulers.computation()).delay(1, TimeUnit.SECONDS))
                .asyncTimeout(Duration.ofSeconds(2))
                .when()
                .subscriber("s1").subscribes()
                .theSource().emits("a")
                .theSource().emits("b")
                .subscriber("s1").waitsforEvents(2)
                .then()
                .subscriber("s1")
                .renderedStream().isEqualTo("[a]-[b]")
                .eventCount().isEqualTo(2);
    }

    @Test(expected = ConditionTimeoutException.class)
    public void asyncWithTimeout() {
        Scenario1<String, String> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .subjectCreated(source -> source.observeOn(Schedulers.computation()).delay(10, TimeUnit.SECONDS))
                .asyncTimeout(Duration.ofMillis(500))
                .when()
                .subscriber("s1").subscribes()
                .theSource().emits("a")
                .theSource().emits("b")
                .subscriber("s1").waitsforEvents(2)
                .go();
    }

    @Test
    public void externalResources() {
        AtomicBoolean closed = new AtomicBoolean(false);
        AutoCloseable resource = () -> closed.getAndSet(true);
        Scenario1<String, String> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                    .theResource(() -> resource)
                    .subjectCreated(source -> Observable.just("a", "b"))
                .when()
                    .subscriber("s1").subscribes()
                .then()
                   .subscriber("s1").eventCount().isEqualTo(2);

        assertThat(closed.get()).isTrue();
    }

    @Test
    public void externalResourcesWithHandledError() {
        AtomicBoolean closed = new AtomicBoolean(false);
        AutoCloseable resource = () -> closed.getAndSet(true);
        Scenario1<String, String> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                .subjectCreated(source -> source.map(s -> s))
                .theResource(() -> resource)
                .errorsAreHandled()
                .when()
                .subscriber("s1").subscribes()
                .theSource().errors(new RuntimeException("I'm broken"))
                .go();

        assertThat(closed.get()).isTrue();
    }

    @Test
    public void externalResourcesWithUnhandledError() {
        AtomicBoolean closed = new AtomicBoolean(false);
        AutoCloseable resource = () -> closed.getAndSet(true);
        Scenario1<String, String> testScenario = TestScenario.singleSource();

        try {
            testScenario
                    .given()
                    .subjectCreated(source -> source.map(s -> s))
                    .theResource(() -> resource)
                    .when()
                    .subscriber("s1").subscribes()
                    .theSource().errors(new RuntimeException("I'm broken"))
                    .go();
        } catch (OnErrorNotImplementedException e) {
            // Expected
            assertThat(closed.get()).isTrue();
        }
    }
}
