package uk.camsw.rxscala.test.dsl.dual

import uk.camsw.rxjava.test.dsl.scenario.ExecutionContext

class DualSourceScenario[T1, T2, U] {
  val context = new ExecutionContext[T1, T2, U, Given[T1, T2, U], When[T1, T2, U]]()
  val _given: Given[T1, T2, U] = new Given[T1, T2, U](context)
  val _when: When[T1, T2, U] = new When[T1, T2, U](context)
  context.initSteps(_given, _when)

  def given() = _given
  def when() = _when

}
