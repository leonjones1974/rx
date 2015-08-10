package uk.camsw.rx.test.dsl.source;

import rx.Observable;
import rx.subjects.PublishSubject;
import uk.camsw.rx.test.dsl.given.IGiven;
import uk.camsw.rx.test.dsl.scenario.ExecutionContext;
import uk.camsw.rx.test.dsl.when.IWhen;

public class BaseSource<T, GIVEN extends IGiven, WHEN extends IWhen> implements ISource<T, WHEN> {

    private final ExecutionContext<?, ?, ?, GIVEN, WHEN> context;
    private final PublishSubject<T> publisher;


    public BaseSource(ExecutionContext<?, ?, ?, GIVEN, WHEN> context) {
        this.context = context;
        this.publisher = PublishSubject.create();
    }

    public <T1> BaseSource(PublishSubject<T> customSource, ExecutionContext<?, ?, ?, GIVEN, WHEN> context) {
        this.context = context;
        this.publisher = customSource;
    }

    public WHEN emits(T event) {
        context.addCommand(c -> publisher.onNext(event));
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
