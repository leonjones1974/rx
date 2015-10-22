package uk.camsw.rxscala.test.dsl.dual

import rx.lang.scala
import rx.lang.scala.JavaConversions._
import rx.lang.scala.Observable
import rx.subjects.PublishSubject
import uk.camsw.rxjava.test.dsl.given.BaseGiven
import uk.camsw.rxjava.test.dsl.scenario.ExecutionContext

class Given[T1, T2, U](ctx: ExecutionContext[T1, T2, U, Given[T1, T2, U], When[T1, T2, U]]) extends BaseGiven[U, Given[T1, T2, U], When[T1, T2, U]](ctx) {

  override def when(): When[T1, T2, U] = ctx.getWhen

  def theStreamUnderTest(f: (Observable[T1], Observable[T2], scala.Scheduler) => Observable[U]) = {
    val source1: Observable[T1] = ctx.getSource1.asObservable()
    val source2: Observable[T2] = ctx.getSource2.asObservable()
    val sut: Observable[U] = f(source1, source2, ctx.getScheduler)
    ctx.setStreamUnderTest(toJavaObservable[U](sut))
    this
  }

  def theStreamUnderTest(f: () => Observable[U]) = {
    ctx.setStreamUnderTest(toJavaObservable[U](f()))
    this
  }

}