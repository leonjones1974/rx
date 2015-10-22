package uk.camsw.rxjava.test.dsl.source;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import uk.camsw.rxjava.test.dsl.given.IGiven;
import uk.camsw.rxjava.test.dsl.scenario.ExecutionContext;
import uk.camsw.rxjava.test.dsl.when.IWhen;

public class BaseSource<T, GIVEN extends IGiven, WHEN extends IWhen> implements ISource<T, WHEN> {

    private final ExecutionContext<?, ?, ?, GIVEN, WHEN> context;
    private final Subject<T, T> publisher;


    public BaseSource(ExecutionContext<?, ?, ?, GIVEN, WHEN> context) {
        this.context = context;
        this.publisher = PublishSubject.create();
    }

    public BaseSource(Subject<T, T> customSource, ExecutionContext<?, ?, ?, GIVEN, WHEN> context) {
        this.context = context;
        this.publisher = customSource;
    }

    public WHEN emits(T event) {
        context.addCommand(c -> {
            publisher.onNext(event);
        });
        return context.getWhen();
    }

    public WHEN completes() {
        context.addCommand(c -> publisher.onCompleted());
        return context.getWhen();
    }

    public WHEN errors(Throwable t) {
        context.addCommand(c -> publisher.onError(t));
        return context.getWhen();
    }

    public Observable<T> asObservable() {
        return publisher;
    }

}
