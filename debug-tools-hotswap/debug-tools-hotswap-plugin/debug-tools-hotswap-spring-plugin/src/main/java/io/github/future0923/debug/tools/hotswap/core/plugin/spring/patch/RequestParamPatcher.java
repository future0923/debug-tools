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
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * <a href="https://github.com/future0923/debug-tools/issues/210">issue210</a>
 *
 * @author future0923
 */
public class RequestParamPatcher {

    @OnClassLoadEvent(classNameRegexp = "org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver")
    public static void patchAbstractNamedValueMethodArgumentResolver(CtClass ctClass, ClassPool classPool) throws CannotCompileException, NotFoundException {
        CtMethod getNamedValueInfo = ctClass.getDeclaredMethod("getNamedValueInfo", new CtClass[]{classPool.get("org.springframework.core.MethodParameter")});
        getNamedValueInfo.insertBefore("{" +
                "   this.namedValueInfoCache.remove($1);" +
                "}");
    }

    @OnClassLoadEvent(classNameRegexp = "org.springframework.core.LocalVariableTableParameterNameDiscoverer")
    public static void patchLocalVariableTableParameterNameDiscoverer(CtClass ctClass, ClassPool classPool) throws CannotCompileException, NotFoundException {
        try {
            CtMethod getParameterNames = ctClass.getDeclaredMethod("doGetParameterNames", new CtClass[]{classPool.get("java.lang.reflect.Executable")});
            getParameterNames.setBody("{" +
                    "   Class declaringClass = $1.getDeclaringClass();" +
                    "   java.util.Map map = this.inspectClass(declaringClass);" +
                    "   if (map != NO_DEBUG_INFO_MAP) {" +
                    "       return (String[])map.get($1);" +
                    "   }" +
                    "   return null;" +
                    "}");
        } catch (Exception e) {
        }
    }
}
