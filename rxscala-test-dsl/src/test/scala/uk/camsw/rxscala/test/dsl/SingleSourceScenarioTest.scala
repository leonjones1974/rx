package uk.camsw.rxscala.test.dsl

import java.time
import java.util.concurrent.atomic.AtomicBoolean

import com.jayway.awaitility.core.ConditionTimeoutException
import org.scalatest.{FunSpec, Matchers}
import rx.exceptions.OnErrorNotImplementedException
import rx.lang.scala.ImplicitFunctionConversions._
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.ComputationScheduler
import uk.camsw.rxscala.test.dsl.TestScenario._

import scala.concurrent.duration._

class SingleSourceScenarioTest
  extends FunSpec
  with Matchers {

  describe("A single source scenario") {
    it("should support a simple scenario") {


      TestScenario.singleSource[String, Int]()
        .given()
        .theStreamUnderTest((source, _) => source.map(s => Integer.parseInt(s) + 1))

        .when()
        .theSubscriber().subscribes()
        .theSource().emits("1")
        .theSource().emits("2")
        .theSource().completes()

        .so()
        .theSubscriber()
        .eventCount().isEqualTo(2)
        .event(0).isEqualTo(2)
        .event(1).isEqualTo(3)
    }

    it("should support multiple subscribers") {
      TestScenario.singleSource[String, Int]()
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

        .so()
        .subscriber("s1")
        .event(0).isEqualTo(2)
        .event(1).isEqualTo(3)
        .eventCount().isEqualTo(2)
        .and()
        .subscriber("s2")
        .event(0).isEqualTo(3)
        .eventCount().isEqualTo(1)
    }

    it("should support unsubscribe") {
      TestScenario.singleSource[String, Int]()
        .given()
        .theStreamUnderTest((source, _) => source.map(s => Integer.parseInt(s) + 1))

        .when()
        .subscriber("s1").subscribes()
        .theSource().emits("1")
        .subscriber("s1").unsubscribes()
        .theSource().emits("2")

        .so()
        .subscriber("s1")
        .eventCount().isEqualTo(1)
    }

    it("should support completion") {
      TestScenario.singleSource[String, Int]()
        .given()
        .theStreamUnderTest((source, _) => source.map(s => Integer.parseInt(s) + 1))

        .when()
        .subscriber("s1").subscribes()
        .theSource().emits("1")
        .theSource().completes()

        .so()
        .subscriber("s1")
        .isErrored.isFalse
        .completedCount().isEqualTo(1)
    }

    it("should capture errors") {
      TestScenario.singleSource[String, Int]()
        .given()
        .theStreamUnderTest((source, _) => source.map(s => Integer.parseInt(s) + 1))
        .errorsAreHandled()

        .when()
        .subscriber("s1").subscribes()
        .theSource().emits("1")
        .theSource().errors(new IllegalArgumentException("oh no"))

        .so()
        .subscriber("s1")
        .isErrored.isTrue
        .errorClass().isAssignableFrom(classOf[IllegalArgumentException])
        .errorMessage().isEqualTo("oh no")
    }

    it("should support raise uncaptured errors") {
      intercept[OnErrorNotImplementedException] {
        TestScenario.singleSource[String, Int]()
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
      TestScenario.singleSource[String, Seq[String]]()
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

        .so()
        .subscriber("s1")
        .eventCount().isEqualTo(2)
        .event(0).isEqualTo(Seq("1a", "1b", "1c"))
        .event(1).isEqualTo(Seq("2a", "2b"))
    }

    it("should support stream rendering") {
      TestScenario.singleSource[Integer, String]()
        .given()
        .theStreamUnderTest((source, _) => source.map(n => if (n == 0) "a" else "B"))
        .theRenderer((event: String) => s"'$event'")

        .when()
        .subscriber("s1").subscribes()
        .theSource().emits(0)
        .theSource().emits(1)
        .theSource().completes()

        .so()
        .subscriber("s1")
        .eventCount().isEqualTo(2)
        .renderedStream().isEqualTo("['a']-['B']-|")
        .completedCount().isEqualTo(1)
    }

    it("should support stream rendering with errors") {
      TestScenario.singleSource[Integer, String]()
        .given()
        .theStreamUnderTest((source, _) => source.map(n => if (n == 0) "a" else "B"))
        .errorsAreHandled()
        .theRenderer((event: String) => s"'$event'")

        .when()
        .subscriber("s1").subscribes()
        .theSource().emits(0)
        .theSource().emits(1)
        .theSource().errors(new RuntimeException("I'm broken"))

        .so()
        .subscriber("s1")
        .renderedStream().isEqualTo("['a']-['B']-X[RuntimeException: I'm broken]")
        .isErrored.isTrue
        .eventCount().isEqualTo(2)
    }

    it("should support asyn wait") {
      TestScenario.singleSource[String, String]()
        .given()
        .theStreamUnderTest((source, _) => source.observeOn(ComputationScheduler()).delay(1 second))
        .asyncTimeoutOf(2 seconds)

        .when()
        .subscriber("s1").subscribes()
        .theSource().emits("a")
        .theSource().emits("b")
        .subscriber("s1").waitsForEvents(2)

        .so()
        .subscriber("s1")
        .renderedStream().isEqualTo("[a]-[b]")
        .eventCount().isEqualTo(2)
    }

    it("should support asyn wait with timeout") {
      intercept[ConditionTimeoutException] {
        TestScenario.singleSource[String, String]()
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

    it("should support custom actions") {
      val s1 = new AtomicBoolean(false)
      TestScenario.singleSource[String, String]()
        .when()
        .actionIsPerformed(() => s1.set(true))

        .go()

      s1.get() shouldBe true
    }

    it("should support all events match") {
      TestScenario.singleSource[Int, Int]()
        .given()
        .theStreamUnderTest((source, _) => source)

        .when()
        .theSubscriber().subscribes()
        .theSource().emits(2)
        .theSource().emits(3)
        .theSource().emits(4)

        .so()
        .theSubscriber()
        .receivedOnlyEventsMatching((n: Int) => n >= 2 && n <= 4, "Event must be between 2 and 4 inclusive")
    }

    it("should support at least one event matches") {
      TestScenario.singleSource[Int, Int]()
        .given()
        .theStreamUnderTest((source, _) => source)

        .when()
        .theSubscriber().subscribes()
        .theSource().emits(2)
        .theSource().emits(3)
        .theSource().emits(4)

        .so()
        .theSubscriber()
        .receivedAtLeastOneMatch((n: Int) => n == 2, "Events should contain 2")
        .receivedAtLeastOneMatch((n: Int) => n == 3, "Events should contain 3")
        .receivedAtLeastOneMatch((n: Int) => n == 4, "Events should contain 4")
    }

    it("should support inlined assertions using default subscriber") {
      TestScenario.singleSource[Int, Int]()
        .given()
        .theStreamUnderTest((source, _) => source)

        .when()
        .theSubscriber().subscribes()
        .theSource().emits(2)
        .checkSubscriber(s => s.eventCount().isEqualTo(1))
        .theSource().emits(3)
        .checkSubscriber(s => s.eventCount().isEqualTo(2))
        .theSource().emits(4)

        .so()
        .theSubscriber()
        .receivedAtLeastOneMatch((n: Int) => n == 2, "Events should contain 2")
        .receivedAtLeastOneMatch((n: Int) => n == 3, "Events should contain 3")
        .receivedAtLeastOneMatch((n: Int) => n == 4, "Events should contain 4")
    }
  }

  it("should support inlined assertions without requiring subscriber") {
    val signal: AtomicBoolean = new AtomicBoolean(false)

    TestScenario.singleSource[Int, Int]()
      .given()
      .theStreamUnderTest((source, _) => source)

      .when()
      .theActionIsPerformed(() => signal.set(true))
      .checkF(signal.get() shouldBe true)
      .checkF(signal.set(false))

      .go()

    signal.get shouldBe false // Double check to get red/green test
  }

  it("should support inlined assertions using named subscriber") {
    TestScenario.singleSource[Int, Int]()
      .given()
      .theStreamUnderTest((source, _) => source)

      .when()
      .theSubscriber("s1").subscribes()
      .theSubscriber("s2").subscribes()
      .theSource().emits(2)
      .check("s1")(s => s.eventCount().isEqualTo(1))
      .check("s2")(s => s.eventCount().isEqualTo(1))
      .subscriber("s2").unsubscribes()
      .theSource().emits(3)
      .check("s1")(s => s.eventCount().isEqualTo(2))
      .check("s2")(s => s.eventCount().isEqualTo(1))
      .theSource().emits(4)

      .so()
      .theSubscriber("s1")
      .receivedAtLeastOneMatch((n: Int) => n == 2, "Events should contain 2")
      .receivedAtLeastOneMatch((n: Int) => n == 3, "Events should contain 3")
      .receivedAtLeastOneMatch((n: Int) => n == 4, "Events should contain 4")
  }

  it("should support stream under test, passed by name") {
    TestScenario.singleSource[Int, Int]()
      .given()
      .theStreamUnderTest(Observable.just(1, 2))

      .when()
      .theSubscriber().subscribes()

      .so()
      .theSubscribers().renderedStream().isEqualTo("[1]-[2]-|")
  }

  it("should support actions, passed by name") {
    val s1 = new AtomicBoolean(false)
    TestScenario.singleSource[String, String]()
      .when()
      .actionIsPerformed(s1.set(true))
      .checkF(s1.get() shouldBe true)

      .go()
  }

  it("should support actions (aliased), passed by name") {
    val s1 = new AtomicBoolean(false)
    TestScenario.singleSource[String, String]()
      .when()
      .doAction(s1.set(true))
      .checkF(s1.get() shouldBe true)

      .go()
  }

  ignore("should support sleep - manual test") {
    TestScenario.singleSource[String, String]()
      .given()
      .theStreamUnderTest((source, _) => source.doOnUnsubscribe(println("Unsubscribed")))

      .when()
      .subscriber("s1").subscribes()
      .doAction(println("Before sleep"))
      .sleepFor(5 seconds)
      .doAction(println("After sleep"))
      .subscriber("s1").unsubscribes()

      .go()

  }

}