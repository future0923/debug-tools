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
package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.patch;

import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;

/**
 * Dynamic多数据源支持@DS注解热重载
 *
 * @author future0923
 */
public class DynamicPatcher {

    /**
     * 修改DataSourceClassResolver.findKey方法，不从缓存中加载数据源
     */
    @OnClassLoadEvent(classNameRegexp = "com.baomidou.dynamic.datasource.support.DataSourceClassResolver")
    public static void patchDataSourceClassResolver(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtClass methodCtClass = classPool.get("java.lang.reflect.Method");
        CtClass objectCtClass = classPool.get("java.lang.Object");
        CtClass classCtClass = classPool.get("java.lang.Class");
        try {
            // ds 4.1
            CtMethod findKey = ctClass.getDeclaredMethod("findKey", new CtClass[]{methodCtClass, objectCtClass});
            findKey.setBody("{" +
                    "   if ($1.getDeclaringClass() == java.lang.Object.class) {" +
                    "       return \"\";" +
                    "   }" +
                    "   java.lang.String ds = computeDatasource($1, $2);" +
                    "   if (ds == null) {" +
                    "       return \"\";" +
                    "   }" +
                    "   return ds;" +
                    "}");
        } catch (NotFoundException e) {
            // ds 4.3
            CtMethod findKey = ctClass.getDeclaredMethod("findKey", new CtClass[]{methodCtClass, objectCtClass, classCtClass});
            findKey.setBody("{" +
                    "   if ($1.getDeclaringClass() == java.lang.Object.class) {" +
                    "       return \"\";" +
                    "   }" +
                    "   java.lang.String ds = computeDatasource($1, $2, $3);" +
                    "   if (ds == null) {" +
                    "       return \"\";" +
                    "   }" +
                    "   return ds;" +
                    "}");
        }
    }
}
