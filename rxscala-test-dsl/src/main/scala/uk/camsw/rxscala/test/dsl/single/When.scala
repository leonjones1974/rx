package uk.camsw.rxscala.test.dsl.single

import uk.camsw.rxjava.test.dsl.scenario.ExecutionContext
import uk.camsw.rxjava.test.dsl.when.BaseWhen

class When[T1, U](ctx: ExecutionContext[T1, _, U, Given[T1, U], When[T1, U]]) extends BaseWhen[U, When[T1, U]](ctx) {

  val _source = ctx.getSource1

  def theSource() = _source
}