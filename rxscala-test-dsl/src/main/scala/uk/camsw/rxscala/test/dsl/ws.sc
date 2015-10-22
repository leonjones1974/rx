import rx.lang.scala.schedulers.ImmediateScheduler
import rx.lang.scala.{Scheduler, Observable}


  def func(f: Int => Int) = {
    f(10)
  }

  def func(f: (Int, Int) => Int) = {
    f(10, 10)
  }

  def func(f: Observable[Int] => Observable[String]) = {
    f(Observable.just(1))
  }

  def func(f: Function1[Observable[Int], Scheduler] => Observable[String]) = {
//    f(Observable.just(1), ImmediateScheduler())
  }

  func((n: Int) => 10)
  func((n: Int, y: Int) => 10)



