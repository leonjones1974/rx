package com.cam.rx.capture.instr;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.Descriptor;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
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
                    for (CtMethod method : methods) {

                        if ("rx.Observable".equals(method.getReturnType().getName())) {

                            String inst = "System.out.println(\"" + method.getName() + "\");";
                            method.insertBefore(inst);
                            System.out.println("Method: " + method.getName());
                            MethodInfo methodInfo = method.getMethodInfo();
                            LocalVariableAttribute table = (LocalVariableAttribute) methodInfo.getCodeAttribute().getAttribute(LocalVariableAttribute.tag);
                            int index = 1;
                            for (CtClass paramClass : method.getParameterTypes()) {
                                System.out.println("\tparam = " + paramClass.getName());
                                if (paramClass.getName().equals("rx.functions.Func1") && "map".equals(method.getName())) {
                                    System.out.println("I got a func1@" + index);
                                    if (table != null) {
                                            int varIndex = table.nameIndex(index);
                                            String variableName = methodInfo.getConstPool().getUtf8Info(varIndex);
                                            System.out.println("\t\t\tname = " + variableName);
                                            method.insertBefore("func = new com.cam.rx.capture.instr.Func1Wrapper(func);");
                                    }
                                }
                                index++;
                            }


                        }
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
            return byteCode;
        }
    }

    private Func1 func1 = new Func1() {
        @Override
        public Object call(Object o) {
            return "s";
        }
    };
}
