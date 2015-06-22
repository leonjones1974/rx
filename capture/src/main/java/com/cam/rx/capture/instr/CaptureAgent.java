package com.cam.rx.capture.instr;

import com.sun.tools.attach.VirtualMachine;
import javassist.*;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import rx.Observable;
import rx.functions.Func1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CaptureAgent {

    public static boolean initialized = false;


    public static void premain(String agentArgs, Instrumentation instr) {
        System.out.println("EXECUTING PRE-MAIN");
        instr.addTransformer(new OperatorInterceptor());
        initialized = true;
    }

    public static void agentmain(String args, Instrumentation instr) {
        System.out.println("EXECUTING AGENT MAIN");
        instr.addTransformer(new OperatorInterceptor());
        initialized = true;
    }

    /**
     * Programmatic hook to dynamically load javaagent at runtime.
     */
    public static void initialize() {
        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');
        String pid = nameOfRunningVM.substring(0, p);

        try {
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent("/home/leonjones/workspace/cam/rx/capture/build/libs/capture-1.0-SNAPSHOT.jar", "");
            vm.detach();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class OperatorInterceptor implements ClassFileTransformer {

        @Override
        public byte[] transform(ClassLoader cl, String className, Class<?> clazz, ProtectionDomain pd, byte[] classfileBuffer) throws IllegalClassFormatException {

            byte[] byteCode = classfileBuffer;

            if (className.contains("rx/internal/operators")) {
                System.out.println("Instrumenting......" + className);
                try {
                    ClassPool classPool = ClassPool.getDefault();
                    CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));

                    List<String> interfaces = Arrays.asList(ctClass.getInterfaces()).stream().map(CtClass::getName).collect(Collectors.toList());
                    System.out.println("ctClass = " + interfaces);
                    if (interfaces.contains("rx.Observable$Operator")) {
                        System.out.println("Found operator");
                        CtMethod call = ctClass.getDeclaredMethod("call");
                        System.out.println("call = " + call);
                        String name = ctClass.getName().replace("Operator", "");
                        call.insertAfter("return new com.cam.rx.capture.instr.SubscriberWrapper(\"" + name + "\", (rx.Observer)$_);");
                    }

                    byteCode = ctClass.toBytecode();
                    ctClass.detach();
                    System.out.println("Instrumentation complete.");
                    System.out.println();
                    System.out.println();
                } catch (Throwable ex) {
                    System.out.println("Exception: " + ex);
                    ex.printStackTrace();
                }
            }

            if (className.equals("rx/Observable")) {
                System.out.println("Instrumenting......" + className);
                try {
                    System.out.println("Found Observable");
                    ClassPool classPool = ClassPool.getDefault();
                    CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                    CtClass subscriberClass = classPool.get("rx/Subscriber");

                    CtMethod subscribe = ctClass.getDeclaredMethod("subscribe", new CtClass[]{subscriberClass});
                    System.out.println("subscribe = " + subscribe);
                    subscribe.insertBefore("subscriber = new com.cam.rx.capture.instr.SubscriberWrapper(\"subscriber\", subscriber);");

                    byteCode = ctClass.toBytecode();
                    ctClass.detach();
                    System.out.println("Instrumentation complete.");
                    System.out.println();
                    System.out.println();
                } catch (Throwable ex) {
                    System.out.println("Exception: " + ex);
                    ex.printStackTrace();
                }
            }
            return byteCode;


        }
    }

}
