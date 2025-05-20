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
package io.github.future0923.debug.tools.hotswap.core.javassist.scopedpool;

import java.util.Map;

import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;

/**
 * An interface to <code>ScopedClassPoolRepositoryImpl</code>.
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.4 $
 */
public interface ScopedClassPoolRepository {
    /**
     * Records a factory.
     */
    void setClassPoolFactory(ScopedClassPoolFactory factory);

    /**
     * Obtains the recorded factory.
     */
    ScopedClassPoolFactory getClassPoolFactory();

    /**
     * Returns whether or not the class pool is pruned.
     * 
     * @return the prune.
     */
    boolean isPrune();

    /**
     * Sets the prune flag.
     * 
     * @param prune     a new value.
     */
    void setPrune(boolean prune);

    /**
     * Create a scoped classpool.
     * 
     * @param cl    the classloader.
     * @param src   the original classpool.
     * @return the classpool.
     */
    ScopedClassPool createScopedClassPool(ClassLoader cl, ClassPool src);

    /**
     * Finds a scoped classpool registered under the passed in classloader.
     * 
     * @param cl    the classloader.
     * @return the classpool.
     */
    ClassPool findClassPool(ClassLoader cl);

    /**
     * Register a classloader.
     * 
     * @param ucl   the classloader.
     * @return the classpool.
     */
    ClassPool registerClassLoader(ClassLoader ucl);

    /**
     * Get the registered classloaders.
     * 
     * @return the registered classloaders.
     */
    Map<ClassLoader,ScopedClassPool> getRegisteredCLs();

    /**
     * This method will check to see if a register classloader has been
     * undeployed (as in JBoss).
     */
    void clearUnregisteredClassLoaders();

    /**
     * Unregisters a classpool and unregisters its classloader.
     * 
     * @param cl    the classloader the pool is stored under.
     */
    void unregisterClassLoader(ClassLoader cl);
}
