package uk.camsw.rxscala.test.dsl.single

import java.util.function.Consumer

import uk.camsw.rxjava.test.dsl.KeyConstants
import uk.camsw.rxjava.test.dsl.scenario.ExecutionContext
import uk.camsw.rxjava.test.dsl.subscriber.{ ISubscriber, SubscriberAssertions }
import uk.camsw.rxjava.test.dsl.then.BaseThen
import uk.camsw.rxjava.test.dsl.when.BaseWhen
import uk.camsw.rxscala.test.dsl.TestScenario._

import scala.concurrent.duration.Duration

class When[T1, U](ctx: ExecutionContext[T1, T1, U, Given[T1, U], When[T1, U]]) extends BaseWhen[U, When[T1, U]](ctx) {

  val _source = ctx.getSource1
  val context = ctx

  def theSource() = _source

  override def then() = throw new UnsupportedOperationException("Then not supported in scala api, use so")

  override def go() = so()

  def so() = super.then()

  override def subscriber(id: String): ISubscriber[U, When[T1, U]] = super.subscriber(id)

  override def theSubscriber(): ISubscriber[U, When[T1, U]] = super.theSubscriber()

  def doAction(f: => Unit): When[T1, U] = {
    execute(f)
  }

  def actionIsPerformed(f: => Unit): When[T1, U] = {
    execute(f)
  }

  def checkF(f: => Unit): When[T1, U] = {
    execute(f)
  }

  def check(id: String)(f: SubscriberAssertions[U] => Unit): When[T1, U] = {
    ctx.addCommand(new Consumer[ExecutionContext[T1, T1, U, Given[T1, U], When[T1, U]]] {
      override def accept(t: ExecutionContext[T1, T1, U, Given[T1, U], When[T1, U]]): Unit = {
        f(new BaseThen[U](t).subscriber(id))
      }
    })
    this
  }

  def checkSubscriber(f: SubscriberAssertions[U] => Unit): When[T1, U] = {
    check(KeyConstants.THE_SUBSCRIBER)(f)
  }

  def sleepFor(duration: Duration): When[T1, U] = {
    doSleep(duration)
  }

  def doSleep(duration: Duration): When[T1, U] = {
    sleepFor(duration)
    this
  }

  def print(x: Any): When[T1, U] = {
    execute(println(x))
    this
  }

  private def execute(f: => Unit): When[T1, U] = {
    ctx.addCommand(new Consumer[ExecutionContext[T1, T1, U, Given[T1, U], When[T1, U]]] {
      override def accept(t: ExecutionContext[T1, T1, U, Given[T1, U], When[T1, U]]): Unit = f
    })
    this
  }

}