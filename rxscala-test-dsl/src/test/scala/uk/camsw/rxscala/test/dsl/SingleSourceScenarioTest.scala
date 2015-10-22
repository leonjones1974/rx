package uk.camsw.rxscala.test.dsl


import java.time
import java.util.concurrent.atomic.AtomicBoolean

import com.jayway.awaitility.core.ConditionTimeoutException
import rx.lang.scala.schedulers.ComputationScheduler
import rx.lang.scala.subjects.PublishSubject
import uk.camsw.rxjava.test.dsl.scenario.ExecutionContext

import scala.concurrent.duration._

import org.scalatest.{FunSpec, Matchers}
import rx.exceptions.OnErrorNotImplementedException
import uk.camsw.rxscala.test.dsl.TestScenario._
import rx.lang.scala.JavaConversions._
import rx.lang.scala.ImplicitFunctionConversions._

class SingleSourceScenarioTest
  extends FunSpec
  with Matchers {

  describe("A single source scenario") {
    it("should support a simple scenario") {

      TestScenario.singleSourceScenario[String, Int]()
        .given()
        .theStreamUnderTest((source, _) => source.map(s => Integer.parseInt(s) + 1))

        .when()
        .theSubscriber().subscribes()
        .theSource().emits("1")
        .theSource().emits("2")
        .theSource().completes()

        .then()
        .theSubscriber()
        .eventCount().isEqualTo(2)
        .event(0).isEqualTo(2)
        .event(1).isEqualTo(3)
    }

    it("should support multiple subscribers") {
      TestScenario.singleSourceScenario[String, Int]()
        .given()
        .theStreamUnderTest((source, _) => source.map(s => Integer.parseInt(s) + 1))

        .when()
        .subscriber("s1")
        .subscribes()
        .theSource().emits("1")
        .subscriber("s2")
        .subscribes()
        .theSource()
        .emits("2")

        .then().subscriber("s1")
        .event(0).isEqualTo(2)
        .event(1).isEqualTo(3)
        .eventCount().isEqualTo(2)
        .and()
        .subscriber("s2")
        .event(0).isEqualTo(3)
        .eventCount().isEqualTo(1)
    }

    it("should support unsubscribe") {
      TestScenario.singleSourceScenario[String, Int]()
        .given()
        .theStreamUnderTest((source, _) => source.map(s => Integer.parseInt(s) + 1))

        .when()
        .subscriber("s1").subscribes()
        .theSource().emits("1")
        .subscriber("s1").unsubscribes()
        .theSource().emits("2")

        .then()
        .subscriber("s1")
        .eventCount().isEqualTo(1)
    }

    it("should support completion") {
      TestScenario.singleSourceScenario[String, Int]()
        .given()
        .theStreamUnderTest((source, _) => source.map(s => Integer.parseInt(s) + 1))

        .when()
        .subscriber("s1").subscribes()
        .theSource().emits("1")
        .theSource().completes()

        .then()
        .subscriber("s1")
        .isErrored.isFalse
        .completedCount().isEqualTo(1)
    }

    it("should capture errors") {
      TestScenario.singleSourceScenario[String, Int]()
        .given()
        .theStreamUnderTest((source, _) => source.map(s => Integer.parseInt(s) + 1))
        .errorsAreHandled()

        .when()
        .subscriber("s1").subscribes()
        .theSource().emits("1")
        .theSource().errors(new IllegalArgumentException("oh no"))

        .then()
        .subscriber("s1")
        .isErrored.isTrue
        .errorClass().isAssignableFrom(classOf[IllegalArgumentException])
        .errorMessage().isEqualTo("oh no")
    }

    it("should support raise uncaptured errors") {
      intercept[OnErrorNotImplementedException] {
        TestScenario.singleSourceScenario[String, Int]()
          .given()
          .theStreamUnderTest((source, _) => source.map(s => Integer.parseInt(s) + 1))

          .when()
          .subscriber("s1").subscribes()
          .theSource().emits("1")
          .theSource().errors(new IllegalArgumentException("oh no"))

          .go()
      }
    }

    it("should support temporal operators") {
      TestScenario.singleSourceScenario[String, Seq[String]]()
        .given()
        .theStreamUnderTest((source, scheduler) => source.tumblingBuffer(10 seconds, scheduler))

        .when()
        .subscriber("s1").subscribes()
        .theSource().emits("1a")
        .theSource().emits("1b")
        .theSource().emits("1c")
        .time().advancesBy(11 seconds)
        .theSource().emits("2a")
        .theSource().emits("2b")
        .theSource().completes()

        .then()
        .subscriber("s1")
        .eventCount().isEqualTo(2)
        .event(0).isEqualTo(Seq("1a", "1b", "1c"))
        .event(1).isEqualTo(Seq("2a", "2b"))
    }

    it("should support a custom source") {
      val customSource = PublishSubject[String]()
      TestScenario.singleSourceScenario[String, String]()
        .given()
        .theCustomSource(customSource.asJavaSubject)
        .theStreamUnderTest((source, _) => source.map(s => s.toUpperCase))

        .when()
        .subscriber("s1").subscribes()
        .theActionIsPerformed(() => customSource.onNext("a"))
        .theActionIsPerformed(() => customSource.onNext("b"))
        .theActionIsPerformed(() => customSource.onCompleted())

        .then()
        .subscriber("s1")
        .renderedStream().isEqualTo("[A]-[B]-|")
    }

    it("should support stream rendering") {
      TestScenario.singleSourceScenario[Integer, String]()
        .given()
        .theStreamUnderTest((source, _) => source.map(n => if (n == 0) "a" else "B"))
        .theRenderer((event: String) => s"'$event'")

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
    }

    it("should support stream rendering with errors") {
      TestScenario.singleSourceScenario[Integer, String]()
        .given()
        .theStreamUnderTest((source, _) => source.map(n => if (n == 0) "a" else "B"))
        .errorsAreHandled()
        .theRenderer((event: String) => s"'$event'")

        .when()
        .subscriber("s1").subscribes()
        .theSource().emits(0)
        .theSource().emits(1)
        .theSource().errors(new RuntimeException("I'm broken"))

        .then()
        .subscriber("s1")
        .renderedStream().isEqualTo("['a']-['B']-X[RuntimeException: I'm broken]")
        .isErrored.isTrue
        .eventCount().isEqualTo(2)
    }

    it("should support asyn wait") {
      TestScenario.singleSourceScenario[String, String]()
        .given()
        .theStreamUnderTest((source, _) => source.observeOn(ComputationScheduler()).delay(1 second))
        .asyncTimeoutOf(2 seconds)

        .when()
        .subscriber("s1").subscribes()
        .theSource().emits("a")
        .theSource().emits("b")
        .subscriber("s1").waitsForEvents(2)

        .then()
        .subscriber("s1")
        .renderedStream().isEqualTo("[a]-[b]")
        .eventCount().isEqualTo(2)
    }

    it("should support asyn wait with timeout") {
      intercept[ConditionTimeoutException] {
        TestScenario.singleSourceScenario[String, String]()
          .given()
          .theStreamUnderTest((source, _) => source.observeOn(ComputationScheduler()).delay(10 seconds))
          .asyncTimeoutOf(500 milliseconds)

          .when()
          .subscriber("s1").subscribes()
          .theSource().emits("a")
          .theSource().emits("b")
          .subscriber("s1").waitsForEvents(2)

          .go()
      }
    }

    it ("should support custom actions") {
      val s1 = new AtomicBoolean(false)
      TestScenario.singleSourceScenario[String, String]()
        .when()
        .actionIsPerformed(() => s1.set(true))

        .go()

      s1.get() shouldBe true
    }
  }
}
