/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.server.trace;

import io.github.future0923.debug.tools.base.hutool.core.util.BooleanUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.tools.base.trace.MethodTrace;
import io.github.future0923.debug.tools.common.dto.TraceMethodDTO;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.vm.JvmToolsUtils;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.Opcode;
import lombok.Getter;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author future0923
 */
public class MethodTraceClassFileTransformer {

    /**
     * 已经追踪的方法信息
     * key:方法唯一签名
     * value:可以用来还原为之前的信息
     */
    private static final Map<String, ResettableClassFileTransformer> RESETTABLE_CLASS_FILE_TRANSFORMER_MAP = new ConcurrentHashMap<>();

    /**
     * 忽略的方法
     */
    @Getter
    private static final Set<String> IGNORED_METHOD_SET = new HashSet<>();

    /**
     * 忽略的类路径
     */
    @Getter
    private static final Set<String> IGNORED_CLASS_SET = new HashSet<>();

    private static final String TRACE_MYBATIS_CLASS_NAME = "org.apache.ibatis.binding.MapperProxy";

    private static final String TRACE_MYBATIS_METHOD_NAME = "invoke";

    public static void traceMethod(ClassLoader classLoader, Class<?> targetClass, Method targetMethod, TraceMethodDTO traceMethodDTO) throws Exception {
        ClassPool classPool = getClassPool();
        CtClass ctClass = classPool.get(targetClass.getName());
        String methodDescription = getDescriptor(classPool, targetMethod);
        redefineMethod(classLoader, classPool, ctClass, targetMethod.getName(), methodDescription, traceMethodDTO.getTraceMaxDepth() == null ? 1 : traceMethodDTO.getTraceMaxDepth());
        redefineMyBatisMethod(classLoader, classPool, traceMethodDTO.getTraceMyBatis());
        MethodTrace.setTraceSqlStatus(traceMethodDTO.getTraceSQL());
    }

    public static void cancelTraceMethod(String className, String methodName, String methodDescription) throws Exception {
        String qualifierNameKey = getQualifierNameKey(className, methodName, methodDescription);
        RESETTABLE_CLASS_FILE_TRANSFORMER_MAP.computeIfPresent(qualifierNameKey, (k, transformer) -> {
            transformer.reset(DebugToolsBootstrap.INSTANCE.getInstrumentation(), AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);
            // 返回 null 表示移除
            return null;
        });
        IGNORED_METHOD_SET.add(qualifierNameKey);
    }

    private static void redefineMyBatisMethod(ClassLoader classLoader, ClassPool classPool, Boolean traceMyBatis) throws Exception {
        Class<?> clazz = classLoader.loadClass(TRACE_MYBATIS_CLASS_NAME);
        String methodDescription = getDescriptor(classPool, ReflectUtil.getMethodByName(clazz, TRACE_MYBATIS_METHOD_NAME));
        String qualifierNameKey = getQualifierNameKey(TRACE_MYBATIS_CLASS_NAME, TRACE_MYBATIS_METHOD_NAME, methodDescription);
        if (BooleanUtil.isTrue(traceMyBatis)) {
            RESETTABLE_CLASS_FILE_TRANSFORMER_MAP.computeIfAbsent(
                    qualifierNameKey,
                    key -> redefineClass(TRACE_MYBATIS_CLASS_NAME, TRACE_MYBATIS_METHOD_NAME, methodDescription)
            );
        } else {
            RESETTABLE_CLASS_FILE_TRANSFORMER_MAP.computeIfPresent(qualifierNameKey, (k, transformer) -> {
                transformer.reset(DebugToolsBootstrap.INSTANCE.getInstrumentation(), AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);
                // 返回 null 表示移除
                return null;
            });
        }
    }

    private static void redefineMethod(ClassLoader classLoader, ClassPool classPool, CtClass ctClass, String methodName, String methodDescription, int maxDepth) throws Exception {
        if (maxDepth - 1 < 0) {
            return;
        }
        String className = ctClass.getName();
        if (className.startsWith("java.")) {
            return;
        }
        if (className.startsWith("javax.")) {
            return;
        }
        if (!className.startsWith("io.github.future0923.debug.tools.test")) {
            return;
        }
        for (String ignoreClassName : IGNORED_CLASS_SET) {
            if (className.startsWith(ignoreClassName)) {
                return;
            }
        }
        String qualifierNameKey = getQualifierNameKey(className, methodName, methodDescription);
        if (IGNORED_METHOD_SET.contains(qualifierNameKey)) {
            return;
        }
        CtMethod[] ctMethod = getCtMethod(ctClass, methodName, methodDescription);
        for (CtMethod method : ctMethod) {
            CodeAttribute codeAttribute = method.getMethodInfo().getCodeAttribute();
            // 接口方法、抽象方法、native方法 codeAttribute为null
            if (codeAttribute == null) {
                Class<?> targetClass = classLoader.loadClass(className);
                // 接口
                if (targetClass.isInterface()) {
                    Set<Class<?>> childClassSet = new HashSet<>();
                    Object[] instances = JvmToolsUtils.getInstances(targetClass);
                    for (Object instance : instances) {
                        childClassSet.add(instance.getClass());
                    }
                    for (Class<?> childClass : childClassSet) {
                        RESETTABLE_CLASS_FILE_TRANSFORMER_MAP.computeIfAbsent(
                                getQualifierNameKey(childClass.getName(), methodName, methodDescription),
                                key -> redefineClass(childClass.getName(), methodName, methodDescription)
                        );
                    }
                }
                // 抽象方法
                if (Modifier.isAbstract(method.getModifiers())) {
                    // TODO
                }
                continue;
            }
            CodeIterator iterator = codeAttribute.iterator();
            ConstPool constPool = method.getMethodInfo().getConstPool();
            while (iterator.hasNext()) {
                int index = iterator.next();
                int opcode = iterator.byteAt(index);
                if (Opcode.INVOKEVIRTUAL == opcode
                        || Opcode.INVOKESPECIAL == opcode
                        || Opcode.INVOKESTATIC == opcode
                        || Opcode.INVOKEINTERFACE == opcode) {
                    int target = iterator.s16bitAt(index + 1);
                    redefineMethod(
                            classLoader,
                            classPool,
                            classPool.get(constPool.getMethodrefClassName(target)),
                            constPool.getMethodrefName(target),
                            constPool.getMethodrefType(target),
                            maxDepth - 1);
                }
            }
            RESETTABLE_CLASS_FILE_TRANSFORMER_MAP.computeIfAbsent(
                    qualifierNameKey,
                    key -> redefineClass(className, methodName, methodDescription)
            );
        }
    }

    private static ResettableClassFileTransformer redefineClass(String className, String methodName, String methodDescription) {
        return new AgentBuilder.Default(new ByteBuddy().with(TypeValidation.DISABLED))
                .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
                .disableClassFormatChanges()
                .type(ElementMatchers.named(className))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) -> builder.visit(Advice.to(Interceptor.class).on(getMethodDescription(methodName, methodDescription)))).installOn(DebugToolsBootstrap.INSTANCE.getInstrumentation());
    }

    private static ElementMatcher<? super MethodDescription> getMethodDescription(String methodName, String methodDescription) {
        ElementMatcher.Junction<NamedElement> methodMatcher = ElementMatchers.named(methodName);
        return methodDescription == null ? methodMatcher : methodMatcher.and(ElementMatchers.hasDescriptor(methodDescription));
    }

    private static CtMethod[] getCtMethod(CtClass ctClass, String methodName, String methodDescribe) {
        try {
            return methodDescribe != null ? new CtMethod[]{ctClass.getMethod(methodName, methodDescribe)} : ctClass.getDeclaredMethods(methodName);
        } catch (NotFoundException e) {
            return new CtMethod[0];
        }
    }

    /**
     * 将 Java 的反射 Method 转为 JVM 方法描述符，例如：(Ljava/lang/String;)V
     */
    public static String getDescriptor(ClassPool classPool, Method method) {
        try {
            // 获取返回类型
            CtClass returnType = toCtClass(method.getReturnType(), classPool);

            // 获取参数类型
            CtClass[] paramTypes = Arrays.stream(method.getParameterTypes())
                    .map(type -> {
                        try {
                            return toCtClass(type, classPool);
                        } catch (NotFoundException e) {
                            throw new RuntimeException("无法找到参数类型：" + type.getName(), e);
                        }
                    })
                    .toArray(CtClass[]::new);

            // 生成方法描述符
            return Descriptor.ofMethod(returnType, paramTypes);

        } catch (Exception e) {
            throw new RuntimeException("生成方法描述符失败", e);
        }
    }

    /**
     * 将 Java 类型转换为 Javassist 的 CtClass，兼容基本类型、数组、对象
     */
    private static CtClass toCtClass(Class<?> clazz, ClassPool pool) throws NotFoundException {
        if (clazz.isPrimitive()) {
            if (clazz == void.class) return CtClass.voidType;
            if (clazz == int.class) return CtClass.intType;
            if (clazz == long.class) return CtClass.longType;
            if (clazz == boolean.class) return CtClass.booleanType;
            if (clazz == byte.class) return CtClass.byteType;
            if (clazz == char.class) return CtClass.charType;
            if (clazz == short.class) return CtClass.shortType;
            if (clazz == float.class) return CtClass.floatType;
            if (clazz == double.class) return CtClass.doubleType;
        }
        if (clazz.isArray()) {
            return pool.get(clazz.getName().replace('$', '.'));
        }
        return pool.get(clazz.getName());
    }

    private static String getQualifierNameKey(String className, String methodName, String methodDescription) {
        return className + "#" + methodName + methodDescription;
    }

    private static ClassPool getClassPool() {
        ClassPool classPool = new ClassPool();
        classPool.appendSystemPath();
        return classPool;
    }

}
