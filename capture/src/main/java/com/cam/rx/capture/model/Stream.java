package com.cam.rx.capture.model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Stream {

    private final String name;
    private final List<Object> events = new ArrayList<>();

    public Stream(String name) {
        this.name = name;
    }

    public void newEvent(Object in, Object out) {
        System.out.println("[" + name +"]: " + in + " -> " + out);
        //todo: real pair
        this.events.add(new Pair<>(in, out));
    }
}
