package com.cam.rx.capture.instr;

import java.lang.instrument.Instrumentation;

public class CaptureAgent {

    public static boolean initialized = false;

    public static void premain(String agentArgs, Instrumentation instr) {
        System.out.println("EXECUTING PRE-MAIN");
        initialized = true;
    }
}
