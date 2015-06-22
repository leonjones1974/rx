package com.cam.rx.capture.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CaptureModel {


    private static Object lock = new Object();
    private static CaptureModel instance;
    private final LoadingCache<String, AtomicInteger> operations;
    private final AtomicInteger eventCount = new AtomicInteger(0);
    private final PublishSubject<Stream> streams = PublishSubject.create();
    private final AtomicBoolean firstStream = new AtomicBoolean(true);

    public static CaptureModel instance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new CaptureModel();
            }
            return instance;
        }
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


    public Stream newStream(Observable<?> source, String name) {


        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (int i = 2; i < stackTrace.length; i++) {
                if (stackTrace[i].getClassName().equals(Observable.class.getName())) return null;
            }

            String qualifiedName = name + "-" + (operations.get(name).incrementAndGet());

            Stream stream = new Stream(source, qualifiedName, firstStream.getAndSet(false));
            streams.onNext(stream);
            return stream;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public Observable<Stream> capturedStreams() {
        return streams;
    }

    public int eventCount() {
        return eventCount.get();
    }
}
