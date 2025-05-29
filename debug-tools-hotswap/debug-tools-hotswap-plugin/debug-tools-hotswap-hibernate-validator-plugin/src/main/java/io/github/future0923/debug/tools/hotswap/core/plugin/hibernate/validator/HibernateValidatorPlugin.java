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
package io.github.future0923.debug.tools.hotswap.core.plugin.hibernate.validator;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;

/**
 * @author future0923
 */
@Plugin(
        name = "HibernateValidator",
        description = "Reload HibernateValidator cache after class definition/change.",
        testedVersions = {"8.0.1.Final", "7.0.5.Final", "6.2.5.Final", "6.0.7.Final"}
)
public class HibernateValidatorPlugin {

    private static final Logger logger = Logger.getLogger(HibernateValidatorPlugin.class);

    /**
     * 低版本BeanMetaDataManager是类，没有接口。如：6.0.7.Final
     */
    @OnClassLoadEvent(classNameRegexp = "org.hibernate.validator.internal.metadata.BeanMetaDataManager")
    public static void patchBeanMetaDataManager(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        extracted(ctClass, classPool, "BeanMetaDataManager");
    }

    /**
     * 高版本BeanMetaDataManager是接口，BeanMetaDataManagerImpl类实现了该接口。如：6.2.5.Final
     */
    @OnClassLoadEvent(classNameRegexp = "org.hibernate.validator.internal.metadata.BeanMetaDataManagerImpl")
    public static void patchBeanMetaDataManagerImpl(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        extracted(ctClass, classPool, "BeanMetaDataManagerImpl");
    }

    private static void extracted(CtClass ctClass, ClassPool classPool, String className) {
        try {
            CtMethod getBeanMetaData = ctClass.getDeclaredMethod("getBeanMetaData", new CtClass[]{classPool.get("java.lang.Class")});
            getBeanMetaData.insertBefore("{" +
                    "   beanMetaDataCache.clear();" +
                    "}");
            logger.info("patch HibernateValidator {} success", className);
        } catch (Exception ignore) {

        }
    }

}
