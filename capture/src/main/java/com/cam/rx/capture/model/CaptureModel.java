package com.cam.rx.capture.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class CaptureModel {

    private static Object lock = new Object();
    private static CaptureModel instance;
    private List<Stream> streams = new ArrayList<>();
    private LoadingCache<String, AtomicInteger> operations;

    public static CaptureModel instance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new CaptureModel();
            }
            return instance;
        }
    }

    public CaptureModel() {
        operations = CacheBuilder.<String, AtomicInteger>newBuilder()
                .build(new CacheLoader<String, AtomicInteger>() {
                    @Override
                    public AtomicInteger load(String name) throws Exception {
                        return new AtomicInteger(0);
                    }
                });
    }

    public void destroy() {
        synchronized (lock) {
            instance = null;
        }
    }

    public Stream newStream(String name) {
        try {
            String qualifiedName = name + "-" + (operations.get(name).incrementAndGet());
            Stream stream = new Stream(qualifiedName);
            streams.add(stream);
            return stream;

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
