package io.github.future0923.debug.tools.test.agent;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

/**
 * @author future0923
 */
public class TestAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                if (className != null) {
                    System.out.println(className);
                }
                return classfileBuffer;
            }
        });
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {

    }

    private void redefineClasses(Instrumentation inst) {
        try {
            // 读取新的字节码文件
            byte[] newClassBytes = readClassBytes("/Users/weilai/Documents/debug-tools/debug-tools-test/debug-tools-test-agent/src/TargetClass.class");
            Class<?> targetClass = TargetClass.class;
            // 创建 ClassDefinition 对象
            ClassDefinition definition = new ClassDefinition(targetClass, newClassBytes);
            // 调用 redefineClasses 方法重新定义类
            inst.redefineClasses(definition);
            System.out.println("Class redefined successfully");
        } catch (IOException e) {
            System.err.println("读取字节码文件失败: " + e.getMessage());
            e.printStackTrace();
        } catch (UnmodifiableClassException e) {
            System.err.println("类无法重新定义: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassFormatError e) {
            System.err.println("字节码格式错误: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("发生其他异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static byte[] readClassBytes(String className) throws IOException {
        try (FileInputStream fis = new FileInputStream(className)) {
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            return buffer;
        }
    }
}
