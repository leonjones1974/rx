package uk.camsw.rxscala.test.dsl

import org.scalatest.{FunSpec, Matchers}
import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject

import rx.lang.scala.ImplicitFunctionConversions._

class DualSourceScenarioTest
  extends FunSpec
  with Matchers {

  describe("A dual source scenario") {

    it("should support same type operators") {
      TestScenario.dualSources[String, String, String]()
        .given()
        .theStreamUnderTest((s1, s2, _) => s1.merge(s2))

        .when()
        .subscriber("s1").subscribes()
        .source1().emits("1")
        .source2().emits("a")
        .source1().emits("2")
        .source2().emits("b")
        .source1().completes()
        .source2().completes()

        .so()
        .subscriber("s1")
        .eventCount().isEqualTo(4)
        .event(0).isEqualTo("1")
        .event(1).isEqualTo("a")
        .event(2).isEqualTo("2")
        .event(3).isEqualTo("b")
    }

    it("should support different type operators") {
      val s1: Observable[String] = Observable.just("a")
      val s2: Observable[Int] = Observable.just(1)
      val s3 = s1.zipWith(s2)((z, n) => "")

      TestScenario.dualSources[String, Int, String]()
        .given()
        .theStreamUnderTest((s1, s2, _) => s1.zipWith(s2)((z, n) => z + n))

        .when()
        .theSubscriber().subscribes()
        .source1().emits("a")
        .source2().emits(1)
        .source1().emits("b")
        .source2().emits(2)
        .source1().completes()

        .so()
        .theSubscribers()
        .eventCount().isEqualTo(2)
        .renderedStream().isEqualTo("[a1]-[b2]-|")
    }
  }
}
