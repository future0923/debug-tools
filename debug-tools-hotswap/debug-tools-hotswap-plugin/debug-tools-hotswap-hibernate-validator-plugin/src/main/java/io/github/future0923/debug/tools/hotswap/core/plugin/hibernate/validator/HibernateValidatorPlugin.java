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
package io.github.future0923.debug.tools.hotswap.core.plugin.hibernate.validator;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

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
