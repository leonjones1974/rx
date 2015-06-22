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
        instr.addTransformer(new StaticStreamCreationInterceptor());
        initialized = true;
    }

    public static void agentmain(String args, Instrumentation instr) {
        System.out.println("EXECUTING AGENT MAIN");
        instr.addTransformer(new OperatorInterceptor());
        instr.addTransformer(new StaticStreamCreationInterceptor());
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
//                System.out.println("Instrumenting......" + className);
                try {
                    ClassPool classPool = ClassPool.getDefault();
                    CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));

                    List<String> interfaces = Arrays.asList(ctClass.getInterfaces()).stream().map(CtClass::getName).collect(Collectors.toList());
//                    System.out.println("ctClass = " + interfaces);
                    if (interfaces.contains("rx.Observable$Operator")) {
//                        System.out.println("Found operator");
                        CtMethod call = ctClass.getDeclaredMethod("call");
//                        System.out.println("call = " + call);
                        String name = ctClass.getName().replace("Operator", "");
                        call.insertAfter("return new com.cam.rx.capture.instr.SubscriberWrapper(\"" + name + "\", (rx.Observer)$_);");
                    }

                    byteCode = ctClass.toBytecode();
                    ctClass.detach();
//                    System.out.println("Instrumentation complete.");
//                    System.out.println();
//                    System.out.println();
                } catch (Throwable ex) {
                    System.out.println("Exception: " + ex);
                    ex.printStackTrace();
                }
            }

            if (className.equals("rx/Observable")) {
//                System.out.println("Instrumenting......" + className);
                try {
                    System.out.println("Found Observable");
                    ClassPool classPool = ClassPool.getDefault();
                    CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                    CtClass subscriberClass = classPool.get("rx/Subscriber");

                    CtMethod subscribe = ctClass.getDeclaredMethod("subscribe", new CtClass[]{subscriberClass});
//                    System.out.println("subscribe = " + subscribe);
//                    subscribe.insertBefore("subscriber = new com.cam.rx.capture.instr.SubscriberWrapper(\"\", subscriber);");
                    subscribe.insertBefore("com.cam.rx.capture.model.CaptureModel.instance().newStream(this, \"subscriber\");");

                    byteCode = ctClass.toBytecode();
                    ctClass.detach();
//                    System.out.println("Instrumentation complete.");
//                    System.out.println();
//                    System.out.println();
                } catch (Throwable ex) {
                    System.out.println("Exception: " + ex);
                    ex.printStackTrace();
                }
            }
            return byteCode;


        }
    }

    public static class StaticStreamCreationInterceptor implements ClassFileTransformer {
        public byte[] transform(ClassLoader loader, String className,
                                Class classBeingRedefined, ProtectionDomain protectionDomain,
                                byte[] classfileBuffer) throws IllegalClassFormatException {
            byte[] byteCode = classfileBuffer;

            if (className.equals("rx/Observable")) {
//                System.out.println("Instrumenting......" + className);
                try {
                    ClassPool classPool = ClassPool.getDefault();
                    CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                    CtMethod[] methods = ctClass.getDeclaredMethods();
                    for (CtMethod method : methods) {

                        if ("rx.Observable".equals(method.getReturnType().getName())) {
                            boolean isStatic = Modifier.isStatic(method.getModifiers());

                            if (!isStatic) {

//                                System.out.println("Method: " + method.getLongName());
                                String inst = "com.cam.rx.capture.model.CaptureModel.instance().newStream($_, \"" + method.getName() + "\");";
                                method.insertAfter(inst);


//                                MethodInfo methodInfo = method.getMethodInfo();
//                                LocalVariableAttribute table = (LocalVariableAttribute) methodInfo.getCodeAttribute().getAttribute(LocalVariableAttribute.tag);
//                                int index = 1;
//                                for (CtClass paramClass : method.getParameterTypes()) {
//
//                                    System.out.println("\tparam = " + paramClass.getName());
//                                    if (paramClass.getName().equals("rx.functions.Func1")) {
//                                        if (table != null) {
//                                            int varIndex = table.nameIndex(index);
//                                            String variableName = methodInfo.getConstPool().getUtf8Info(varIndex);
//                                            System.out.println("\t\t\tFunc1 name = " + variableName);
//                                            method.addLocalVariable("_stream", classPool.getCtClass("com.cam.rx.capture.model.Stream"));
//                                            String inst = "_stream = com.cam.rx.capture.model.CaptureModel.instance().newStream(\"" + method.getName() + "\");";
//                                            method.insertBefore(inst + "; " + variableName + " = new com.cam.rx.capture.instr.Func1Wrapper(" + variableName + ", _stream);");
//                                        }
//                                    }
//                                    index++;
//                                }
                            }
                        }
                    }
                    byteCode = ctClass.toBytecode();
                    ctClass.detach();
//                    System.out.println("Instrumentation complete.");
//                    System.out.println();
//                    System.out.println();
                } catch (Throwable ex) {
                    System.out.println("Exception: " + ex);
                    ex.printStackTrace();
                }
            }
            return byteCode;
        }
    }

}
