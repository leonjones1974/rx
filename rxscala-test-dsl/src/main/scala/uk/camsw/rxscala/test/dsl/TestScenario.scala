package uk.camsw.rxscala.test.dsl

import java.time

import scala.concurrent.duration.Duration

object TestScenario {

  def singleSourceScenario[T1, U]() = new SingleSourceScenario[T1, U]()

  implicit def toJavaDuration(s: Duration) : time.Duration = {
    time.Duration.ofNanos(s.toNanos)
  }
}
