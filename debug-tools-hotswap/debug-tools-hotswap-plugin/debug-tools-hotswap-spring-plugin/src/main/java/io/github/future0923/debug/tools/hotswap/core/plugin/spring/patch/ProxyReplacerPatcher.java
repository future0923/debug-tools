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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.patch;

import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

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