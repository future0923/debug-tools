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
        CtMethod findKey = ctClass.getDeclaredMethod("findKey");
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
    }
}
