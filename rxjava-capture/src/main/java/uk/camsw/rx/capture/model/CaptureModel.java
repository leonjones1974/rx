package uk.camsw.rx.capture.model;

import uk.camsw.rx.capture.instr.CaptureAgent;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subscriptions.Subscriptions;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CaptureModel {

    private static Object lock = new Object();
    private static CaptureModel instance;
    private final LoadingCache<String, AtomicInteger> operations;
    private final AtomicInteger eventCount = new AtomicInteger(0);
    private final ReplaySubject<Stream> streams = ReplaySubject.create();
    private final AtomicBoolean firstStream = new AtomicBoolean(true);
    private final AtomicBoolean capturing = new AtomicBoolean(false);

    public static CaptureModel instance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new CaptureModel();
            }
            return instance;
        }
    }

    public Subscription beginCapture() {
        capturing.set(true);
        return Subscriptions.create(() -> capturing.set(false));
    }

    public Observable<Stream> streams() {
        return streams;
    }

    public int nextEventCount() {
        eventCount.incrementAndGet();
        return eventCount.incrementAndGet();
    }

    public CaptureModel() {
        operations = CacheBuilder.<String, AtomicInteger>newBuilder()
                .build(new CacheLoader<String, AtomicInteger>() {
                    @Override
                    public AtomicInteger load(String name) throws Exception {
                        return new AtomicInteger(0);
                    }
                });
        eventCount.set(0);
    }

    public void destroy() {
        synchronized (lock) {
            instance = null;
        }
    }

    public void newStream(Observable<?> source, String name) {
        if (capturing.get()) {
            try {
                StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
                for (int i = 2; i < stackTrace.length; i++) {
                    if (stackTrace[i].getClassName().equals(Observable.class.getName())) return;
                }
                String qualifiedName = name + "-" + (operations.get(name).incrementAndGet());
                Stream stream = new Stream(source, qualifiedName, firstStream.getAndSet(false));
                streams.onNext(stream);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int eventCount() {
        return eventCount.get();
    }
}
