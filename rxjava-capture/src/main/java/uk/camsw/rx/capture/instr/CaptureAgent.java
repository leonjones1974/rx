package uk.camsw.rx.capture.instr;

import com.sun.tools.attach.VirtualMachine;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.security.ProtectionDomain;

public class CaptureAgent {

    private static final Logger logger = LoggerFactory.getLogger(CaptureAgent.class);
    public static boolean initialized = false;


    public static void premain(String agentArgs, Instrumentation instr) {
        instr.addTransformer(new OperatorInterceptor());
        instr.addTransformer(new StaticStreamCreationInterceptor());
        initialized = true;
    }

    public static void agentmain(String args, Instrumentation instr) {
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
            vm.loadAgent("/home/leonjones/workspace/cam/rx/capture/build/libs/capture-1.2-SNAPSHOT.jar", "");
            vm.detach();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class OperatorInterceptor implements ClassFileTransformer {

        @Override
        public byte[] transform(ClassLoader cl, String className, Class<?> clazz, ProtectionDomain pd, byte[] classfileBuffer) throws IllegalClassFormatException {
            byte[] byteCode = classfileBuffer;

            if (className.equals("rx/Observable")) {
                try {
                    ClassPool classPool = ClassPool.getDefault();
                    CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                    CtClass subscriberClass = classPool.get("rx/Subscriber");
                    CtMethod subscribe = ctClass.getDeclaredMethod("subscribe", new CtClass[]{subscriberClass});
                    subscribe.insertBefore("uk.camsw.rx.capture.model.CaptureModel.instance().newStream(this, \"subscriber\");");
                    byteCode = ctClass.toBytecode();
                    ctClass.detach();
                } catch (Throwable ex) {
                    logger.error(ex);
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
                try {
                    ClassPool classPool = ClassPool.getDefault();
                    CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                    CtMethod[] methods = ctClass.getDeclaredMethods();
                    for (CtMethod method : methods) {

                        if ("rx.Observable".equals(method.getReturnType().getName())) {
                            boolean isStatic = Modifier.isStatic(method.getModifiers());

                            if (!isStatic) {
                                String inst = "uk.camsw.rx.capture.model.CaptureModel.instance().newStream($_, \"" + method.getName() + "\");";
                                method.insertAfter(inst);
                            }
                        }
                    }
                    byteCode = ctClass.toBytecode();
                    ctClass.detach();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
            return byteCode;
        }
    }

}
