package uk.camsw.rxjava.test.dsl.scenario;

import org.junit.Test;
import rx.Observable;
import rx.subjects.PublishSubject;
import uk.camsw.rxjava.test.dsl.TestScenario;

public class DualSourceScenarioTest {
    @Test
    public void merge() {
        DualSourceScenario<String, String, String> testScenario = TestScenario.dualSources();

        testScenario
                .given()
                .theStreamUnderTest(Observable::mergeWith)

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
        DualSourceScenario<String, Integer, String> testScenario = TestScenario.dualSources();

        testScenario
                .given()
                .theStreamUnderTest((s1, s2) -> s1.zipWith(s2, (z, n) -> z + n))
                .theRenderer(s -> s)

                .when()
                .theSubscriber().subscribes()
                .source1().emits("a")
                .source2().emits(1)
                .source1().emits("b")
                .source2().emits(2)
                .source1().completes()

                .then()
                .theSubscribers()
                .eventCount().isEqualTo(2)
                .renderedStream().isEqualTo("[a1]-[b2]-|");
    }

    @Test
    public void zipWithBackPressure() {
        DualSourceScenario<String, Integer, String> testScenario = TestScenario.dualSources();

        testScenario
                .given()
                .theStreamUnderTest((s1, s2) -> s1.zipWith(s2, (z, n) -> z + n))
                .theRenderer(s -> s)

                .when()
                .theSubscriber().subscribes()
                .source1().emits("a")
                .source1().emits("b")
                .source1().emits("c")
                .source2().emits(1)
                .source2().emits(2)
                .source2().emits(3)
                .source1().completes()

                .then()
                .theSubscribers()
                .renderedStream().isEqualTo("[a1]-[b2]-[c3]-|");
    }

    @Test
    public void zipWithBackPressureOnSource2() {
        DualSourceScenario<String, Integer, String> testScenario = TestScenario.dualSources();

        testScenario
                .given()
                .theStreamUnderTest((s1, s2) -> s1.zipWith(s2, (z, n) -> z + n))
                .theRenderer(s -> s)

                .when()
                .theSubscriber().subscribes()
                .source2().emits(1)
                .source2().emits(2)
                .source2().emits(3)
                .source1().emits("a")
                .source1().emits("b")
                .source1().emits("c")
                .source1().completes()

                .then()
                .theSubscribers()
                .renderedStream().isEqualTo("[a1]-[b2]-[c3]-|");
    }

    @Test
    public void customSourceZip() {

        DualSourceScenario<String, Integer, String> testScenario = TestScenario.dualSources();
        PublishSubject<String> source1 = PublishSubject.create();
        PublishSubject<Integer> source2 = PublishSubject.create();

        testScenario
                .given()
                .theCustomSource1(source1)
                .theCustomSource2(source2)
                .theStreamUnderTest((s1, s2) -> s1.zipWith(s2, (z, n) -> z + n))
                .theRenderer(s -> s)

                .when()
                .subscriber("s1").subscribes()
                .theActionIsPerformed(() -> source1.onNext("a"))
                .theActionIsPerformed(() -> source2.onNext(1))
                .theActionIsPerformed(() -> source1.onNext("b"))
                .theActionIsPerformed(() -> source2.onNext(2))
                .theActionIsPerformed(source1::onCompleted)
                .theActionIsPerformed(source2::onCompleted)

                .then()
                .subscriber("s1")
                .eventCount().isEqualTo(2)
                .renderedStream().isEqualTo("[a1]-[b2]-|");

    }

}