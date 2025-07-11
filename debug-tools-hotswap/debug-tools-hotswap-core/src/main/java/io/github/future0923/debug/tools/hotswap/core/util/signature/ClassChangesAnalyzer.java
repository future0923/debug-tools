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
package io.github.future0923.debug.tools.hotswap.core.util.signature;

import io.github.future0923.debug.tools.base.logging.Logger;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;

import java.io.ByteArrayInputStream;

/**
 * 解析Class是否需要进行Bean重新加载{@link #isReloadNeeded}
 * <p>
 * 合成类或生成类不需要
 * <p>
 * 方法体的修改也不需要
 */
public class ClassChangesAnalyzer {

    private static final Logger LOGGER = Logger.getLogger(ClassChangesAnalyzer.class);

    private final ClassPool classPool;

    public ClassChangesAnalyzer(final ClassLoader classLoader) {
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