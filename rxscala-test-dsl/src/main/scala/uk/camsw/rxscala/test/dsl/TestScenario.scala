package uk.camsw.rxscala.test.dsl

import java.time
import java.util.function.{Predicate => JPredicate}

import uk.camsw.rxscala.test.dsl.dual.DualSourceScenario
import uk.camsw.rxscala.test.dsl.single.SingleSourceScenario

import scala.concurrent.duration.Duration
object TestScenario {

  def singleSource[T1, U]() = new SingleSourceScenario[T1, U]()
  def dualSources[T1, T2, U]() = new DualSourceScenario[T1, T2, U]()

  implicit def toJavaDuration(s: Duration) : time.Duration = {
    time.Duration.ofNanos(s.toNanos)
  }

  implicit def toJavaPredicate[A](f: Function1[A, Boolean]) : JPredicate[A] = new JPredicate[A] {
    override def test(a: A): Boolean = f(a)
  }

}
