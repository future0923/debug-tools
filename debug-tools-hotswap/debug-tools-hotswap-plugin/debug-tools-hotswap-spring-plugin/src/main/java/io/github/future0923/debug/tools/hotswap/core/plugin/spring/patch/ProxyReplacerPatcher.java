/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.patch;

import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtConstructor;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtNewMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;

/**
 * SpringBean代理转换，返回被代理之后的bean，代理之后可以重置或重新载入。
 */
public class ProxyReplacerPatcher {

    public static final String FACTORY_METHOD_NAME = "getBean";

    /**
     * 为ctClass动态添加方法，该方法是父类中的方法，可以保持父类的方法，子类还可以增强修改
     *
     * @param ctClass 要添加方法的类
     * @param delegate 父类中的方法
     */
    private static CtMethod overrideMethod(CtClass ctClass, CtMethod delegate) throws NotFoundException, CannotCompileException {
        final CtMethod m = CtNewMethod.delegator(delegate, ctClass);
        ctClass.addMethod(m);
        return m;
    }

    /**
     * 拦截所有的getBean方法，当要获取Bean的时候，替换为可以热重载的Bean
     */
    @OnClassLoadEvent(classNameRegexp = "org.springframework.beans.factory.support.DefaultListableBeanFactory")
    public static void replaceBeanWithProxy(CtClass ctClass) throws NotFoundException, CannotCompileException {
        CtMethod[] methods = ctClass.getMethods();
        for (CtMethod ctMethod : methods) {
            if (!ctMethod.getName().equals(FACTORY_METHOD_NAME)) {
                continue;
            }
            if (!ctClass.equals(ctMethod.getDeclaringClass())) {
                ctMethod = overrideMethod(ctClass, ctMethod);
            }
            StringBuilder methodParamTypes = new StringBuilder();
            for (CtClass type : ctMethod.getParameterTypes()) {
                methodParamTypes.append(type.getName()).append(".class").append(", ");
            }
            // 插入的方法如下
            //if (true) {
            //    return io.github.future0923.debug.tools.hotswap.core.plugin.spring.getbean.ProxyReplacer.register(
            //            $0, // 增强方法的this
            //            $_, // 增强方法返回值
            //            new Class[]{java.lang.Class.class, java.lang.Object[].class}, // 增强方法参数类型
            //            $args // 增强方法的实际入参
            //    );
            //}
            ctMethod.insertAfter("if(true){" +
                    "return io.github.future0923.debug.tools.hotswap.core.plugin.spring.getbean.ProxyReplacer.register($0, $_,new Class[]{"
                    + methodParamTypes.substring(0, methodParamTypes.length() - 2) + "}, $args);" +
                    "}");
        }

    }

    /**
     * 禁用 FastClass.Generator 缓存避免 "IllegalArgumentException: Protected method"
     */
    @OnClassLoadEvent(classNameRegexp = "org.springframework.cglib.reflect.FastClass.Generator")
    public static void replaceSpringFastClassGenerator(CtClass ctClass) throws NotFoundException, CannotCompileException {
        replaceCglibFastClassGenerator(ctClass);
    }

    /**
     * 禁用 FastClass.Generator 缓存避免 "IllegalArgumentException: Protected method"
     */
    @OnClassLoadEvent(classNameRegexp = "net.sf.cglib.reflect.FastClass.Generator")
    public static void replaceCglibFastClassGenerator(CtClass ctClass) throws NotFoundException, CannotCompileException {
        CtConstructor[] constructors = ctClass.getConstructors();
        for (CtConstructor ctConstructor : constructors) {
            ctConstructor.insertAfter("setUseCache(false);");
        }
    }
}