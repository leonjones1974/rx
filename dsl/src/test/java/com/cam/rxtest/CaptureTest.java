package com.cam.rxtest;

import com.cam.rx.capture.instr.CaptureAgent;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CaptureTest {

    @Test
    public void itShould_EnableInstrumentation() {
        assertThat(CaptureAgent.initialized).isTrue();
    }
}
