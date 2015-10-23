package uk.camsw.rxscala.test.dsl.dual

import uk.camsw.rxjava.test.dsl.scenario.ExecutionContext
import uk.camsw.rxjava.test.dsl.when.BaseWhen

class When[T1, T2, U](ctx: ExecutionContext[T1, T2, U, Given[T1, T2, U], When[T1, T2, U]]) extends BaseWhen[U, When[T1, T2, U]](ctx) {

   val _source1 = ctx.getSource1
   val _source2 = ctx.getSource2

   def source1() = _source1
   def source2() = _source2

  override def then() = throw new UnsupportedOperationException("Then not supported in scala api, use so")

  override def go()  = so()

  def so() = super.then()
 }