package uk.camsw.rx.test.dsl.scenario;

import org.junit.Test;
import rx.Observable;
import rx.subjects.PublishSubject;
import uk.camsw.rx.test.dsl.TestScenario;

import static org.junit.Assert.*;

public class DualSourceScenarioTest {
    @Test
    public void merge() {
        DualSourceScenario<String, String, String> testScenario = TestScenario.dualSources();

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
        DualSourceScenario<String, Integer, String> testScenario = TestScenario.dualSources();

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
    public void customSourceZip() {

        DualSourceScenario<String, Integer, String> testScenario = TestScenario.dualSources();
        PublishSubject<String> source1 = PublishSubject.create();
        PublishSubject<Integer> source2 = PublishSubject.create();

        testScenario
                .given()
                .theCustomSource1(source1)
                .theCustomSource2(source2)
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


        PublishSubject<String> customSource = PublishSubject.create();
        TestScenario.<String, String>singleSource()
                .given()
                .theCustomSource(customSource)
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

}