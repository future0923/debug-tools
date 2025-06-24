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
