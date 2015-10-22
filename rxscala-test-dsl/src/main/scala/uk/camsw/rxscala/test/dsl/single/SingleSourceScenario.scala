package uk.camsw.rxscala.test.dsl.single

import uk.camsw.rxjava.test.dsl.scenario.ExecutionContext

class SingleSourceScenario[T1, U] {
  val context = new ExecutionContext[T1, T1, U, Given[T1, U], When[T1, U]]()
  val _given: Given[T1, U] = new Given[T1, U](context)
  val _when: When[T1, U] = new When[T1, U](context)
  context.initSteps(_given, _when)

  def given() = _given
  def when() = _when

}
