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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformer;


import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.LoaderClassPath;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.SpringPlugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.signature.ClassSignatureComparer;

import java.io.ByteArrayInputStream;

/**
 * 解析是否需要Spring进行重新加载{@link #isReloadNeeded}
 * <p>
 * 合成类或生成类不需要
 * <p>
 * 方法体的修改也不需要
 */
public class SpringChangesAnalyzer {
    private static final Logger LOGGER = Logger.getLogger(SpringPlugin.class);

    private final ClassPool classPool;

    public SpringChangesAnalyzer(final ClassLoader classLoader) {
        this.classPool = new ClassPool() {
            @Override
            public ClassLoader getClassLoader() {
                return classLoader;
            }
        };
        classPool.appendSystemPath();
        classPool.appendClassPath(new LoaderClassPath(classLoader));
    }

    public boolean isReloadNeeded(Class<?> classBeingRedefined, byte[] classfileBuffer) {
        // jvm合成的类不需要
        if (classBeingRedefined.isSynthetic() || isSyntheticClass(classBeingRedefined)) {
            return false;
        }
        return classChangeNeedsReload(classBeingRedefined, classfileBuffer);
    }

    private boolean classChangeNeedsReload(Class<?> classBeingRedefined, byte[] classfileBuffer) {
        CtClass makeClass = null;
        try {
            makeClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
            return ClassSignatureComparer.isPoolClassDifferent(classBeingRedefined, classPool);
        } catch (Exception e) {
            LOGGER.error("Error analyzing class {} for reload necessity. Defaulting to yes.", e, classBeingRedefined.getName());
        } finally {
            if (makeClass != null) {
                makeClass.detach();
            }
        }
        return true;
    }

    protected boolean isSyntheticClass(Class<?> classBeingRedefined) {
        return classBeingRedefined.getSimpleName().contains("$$_javassist")
                || classBeingRedefined.getName().startsWith("com.sun.proxy.$Proxy")
                || classBeingRedefined.getSimpleName().contains("$$Enhancer")
                || classBeingRedefined.getSimpleName().contains("$$_jvst") // javassist proxy
                || classBeingRedefined.getSimpleName().contains("$HibernateProxy$")
                ;
    }

}
