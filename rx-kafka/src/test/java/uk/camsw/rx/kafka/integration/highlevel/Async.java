package uk.camsw.rx.kafka.integration.highlevel;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;

/**
 * Created by leonjones on 18/08/15.
 */
public class Async {


    private static PublishSubject<String> publisher = PublishSubject.create();

    public <T> Observable<T> async(Observable<T> source, Func1<T, Boolean> f) {
        return Observable.create(s -> {
            s.add(source.filter(f).take(1).timeout(3, TimeUnit.SECONDS).subscribe(s));
            try {
                Thread.sleep(4000);
//                publisher.onNext("Hello");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }



    public static void main(String[] args) throws InterruptedException {
//        new Async().async(publisher, t -> true)
//                .subscribeOn(Schedulers.io())
//                .subscribe(System.out::println);
//
//        Thread.sleep(5000);


        Observable.just(1, 2, 3)
                .<String>flatMap(n -> {
                    if (n == 1 || n == 3) return Observable.just("a", "b");
                    else return Observable.<String>error(new RuntimeException("blah")).onErrorResumeNext(t -> Observable.just(t.getMessage()));
                }).subscribe(System.out::println);
    }
}
