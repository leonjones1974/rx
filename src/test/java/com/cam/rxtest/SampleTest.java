package com.cam.rxtest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SampleTest {

    private EventSource<String> subject;

    @Before
    public void before() {
        subject = new EventSource<String>();
    }

    @Test
    @Ignore
    public void simpleAction() {
        Context context = subject.subscribe("subscriber1")
                .emit("1")
                .go();


        context.subscriber("subscriber1")
                .eventCount()
                .isEqualTo(1);

        context.subscriber("subscriber1")
                .event(1)
                .equals("subscriber1");

    }

}
