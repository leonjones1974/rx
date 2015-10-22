package uk.camsw.rxscala.test.dsl

import java.time

import rx.lang.scala
import rx.lang.scala.JavaConversions._
import rx.lang.scala.{subjects, Observable}
import rx.subjects.{Subject, PublishSubject}
import uk.camsw.rxjava.test.dsl.given.BaseGiven
import uk.camsw.rxjava.test.dsl.scenario.ExecutionContext

class Given[T1, U](ctx: ExecutionContext[T1, T1, U, Given[T1, U], When[T1, U]]) extends BaseGiven[U, Given[T1, U], When[T1, U]](ctx) {
  val context = ctx

  override def when(): When[T1, U] = context.getWhen

  def theStreamUnderTest(f: (Observable[T1], scala.Scheduler) => Observable[U]) = {
    val source: Observable[T1] = context.getSource1.asObservable()
    val sut: Observable[U] = f(source, context.getScheduler)
    context.setStreamUnderTest(toJavaObservable[U](sut))
    this
  }

  def theStreamUnderTest(f: () => Observable[U]) = {
    context.setStreamUnderTest(toJavaObservable[U](f()))
    this
  }

  def theCustomSource(customSource: PublishSubject[T1] ) = {
    context.setCustomSource1(customSource)
    this
  }
  

}