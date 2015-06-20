package com.cam.rx.capture.instr;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.Descriptor;
import rx.functions.Func1;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class CaptureAgent {

    public static boolean initialized = false;

    public static void premain(String agentArgs, Instrumentation instr) {
        System.out.println("EXECUTING PRE-MAIN");
        instr.addTransformer(new DurationTransformer());
        initialized = true;
    }

    //this class will be registered with instrumentation agent
    public static class DurationTransformer implements ClassFileTransformer {
        public byte[] transform(ClassLoader loader, String className,
                                Class classBeingRedefined, ProtectionDomain protectionDomain,
                                byte[] classfileBuffer) throws IllegalClassFormatException {
            byte[] byteCode = classfileBuffer;

            if (className.equals("rx/Observable")) {
                System.out.println("Instrumenting......" + className);
                try {
                    ClassPool classPool = ClassPool.getDefault();
                    CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                    CtMethod[] methods = ctClass.getDeclaredMethods();
                    System.out.println("methods = " + methods.length);

                    CtClass func1 = classPool.getCtClass("rx/functions/Func1");
                    CtClass observable = classPool.getCtClass("rx/Observable");

                    CtMethod method = ctClass.getMethod("map", Descriptor.ofMethod(observable, new CtClass[]{func1}));
                    System.out.println("method = " + method);
                    method.insertAfter("System.out.println(\"--MAP--\");");
//                    for (CtMethod method : methods) {
//                        System.out.println("method: " + method.getName());
//                        method.addLocalVariable("startTime", CtClass.longType);
//                        method.insertBefore("startTime = System.nanoTime();");
//                        method.insertAfter("System.out.println(\"Execution Duration (nano sec): \"+ (System.nanoTime() - startTime) );");
//                        System.out.println("Instrumented = " + method.getName());
//                    }
                    byteCode = ctClass.toBytecode();
                    ctClass.detach();
                    System.out.println("Instrumentation complete.");
                } catch (Throwable ex) {
                    System.out.println("Exception: " + ex);
                    ex.printStackTrace();
                }
            }
            return byteCode;
        }
    }
}
