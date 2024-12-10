/*
 * Copyright 2013-2024 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtField;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;

/**
 * 对配置占位符进行转换处理，Spring的{@link PlaceholderConfigurerSupport}会对配置占位符进行解析，如解析{@code ${property.name:defaultValue}}这种字符串
 */
public class PlaceholderConfigurerSupportTransformer {

    private static final Logger LOGGER = Logger.getLogger(PlaceholderConfigurerSupportTransformer.class);

    @OnClassLoadEvent(classNameRegexp = "org.springframework.beans.factory.config.PlaceholderConfigurerSupport")
    public static void transform(CtClass clazz, ClassPool classPool) throws NotFoundException, CannotCompileException {
        for (CtClass interfaceClazz : clazz.getInterfaces()) {
            if (interfaceClazz.getName().equals("io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers.api.ValueResolverSupport")) {
                return;
            }
        }
        clazz.addInterface(classPool.get("io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers.api.ValueResolverSupport"));
        clazz.addField(CtField.make("private java.util.List _resolvers;", clazz), "new java.util.ArrayList(2)");
        clazz.addMethod(CtMethod.make("public java.util.List valueResolvers() { return this._resolvers; }", clazz));
        CtMethod ctMethod = clazz.getDeclaredMethod("doProcessProperties", new CtClass[]{classPool.get("org.springframework.beans.factory.config.ConfigurableListableBeanFactory"), classPool.get("org.springframework.util.StringValueResolver")});
        ctMethod.insertBefore(
                "io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload.SpringChangedAgent.collectPlaceholderProperties($1); " +
                        "this._resolvers.add($2);"
        );
        LOGGER.debug("class 'org.springframework.beans.factory.config.PlaceholderConfigurerSupport' patched with placeholder keep.");
    }
}
