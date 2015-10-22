package uk.camsw.rxscala.test.dsl.single

import rx.lang.scala
import rx.lang.scala.JavaConversions._
import rx.lang.scala.Observable
import rx.subjects.PublishSubject
import uk.camsw.rxjava.test.dsl.given.BaseGiven
import uk.camsw.rxjava.test.dsl.scenario.ExecutionContext

class Given[T1, U](ctx: ExecutionContext[T1, T1, U, Given[T1, U], When[T1, U]]) extends BaseGiven[U, Given[T1, U], When[T1, U]](ctx) {

  override def when(): When[T1, U] = ctx.getWhen

  def theStreamUnderTest(f: (Observable[T1], scala.Scheduler) => Observable[U]) = {
    val source: Observable[T1] = ctx.getSource1.asObservable()
    val sut: Observable[U] = f(source, ctx.getScheduler)
    ctx.setStreamUnderTest(toJavaObservable[U](sut))
    this
  }

  def theStreamUnderTest(f: () => Observable[U]) = {
    ctx.setStreamUnderTest(toJavaObservable[U](f()))
    this
  }

}