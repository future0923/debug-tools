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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.LoaderClassPath;

/**
 * An implementation of <code>ScopedClassPoolRepository</code>.
 * It is an singleton.
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.4 $
 */
public class ScopedClassPoolRepositoryImpl implements ScopedClassPoolRepository {
    /** The instance */
    private static final ScopedClassPoolRepositoryImpl instance = new ScopedClassPoolRepositoryImpl();

    /** Whether to prune */
    private boolean prune = true;

    /** Whether to prune when added to the classpool's cache */
    boolean pruneWhenCached;

    /** The registered classloaders */
    protected Map<ClassLoader,ScopedClassPool> registeredCLs = Collections
            .synchronizedMap(new WeakHashMap<ClassLoader,ScopedClassPool>());

    /** The default class pool */
    protected ClassPool classpool;

    /** The factory for creating class pools */
    protected ScopedClassPoolFactory factory = new ScopedClassPoolFactoryImpl();

    /**
     * Get the instance.
     * 
     * @return the instance.
     */
    public static ScopedClassPoolRepository getInstance() {
        return instance;
    }

    /**
     * Singleton.
     */
    private ScopedClassPoolRepositoryImpl() {
        classpool = ClassPool.getDefault();
        // FIXME This doesn't look correct
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        classpool.insertClassPath(new LoaderClassPath(cl));
    }

    /**
     * Returns the value of the prune attribute.
     * 
     * @return the prune.
     */
    @Override
    public boolean isPrune() {
        return prune;
    }

    /**
     * Set the prune attribute.
     * 
     * @param prune     a new value.
     */
    @Override
    public void setPrune(boolean prune) {
        this.prune = prune;
    }

    /**
     * Create a scoped classpool.
     * 
     * @param cl    the classloader.
     * @param src   the original classpool.
     * @return the classpool
     */
    @Override
    public ScopedClassPool createScopedClassPool(ClassLoader cl, ClassPool src) {
        return factory.create(cl, src, this);
    }

    @Override
    public ClassPool findClassPool(ClassLoader cl) {
        if (cl == null)
            return registerClassLoader(ClassLoader.getSystemClassLoader());

        return registerClassLoader(cl);
    }

    /**
     * Register a classloader.
     * 
     * @param ucl       the classloader.
     * @return the classpool
     */
    @Override
    public ClassPool registerClassLoader(ClassLoader ucl) {
        synchronized (registeredCLs) {
            // FIXME: Probably want to take this method out later
            // so that AOP framework can be independent of JMX
            // This is in here so that we can remove a UCL from the ClassPool as
            // a
            // ClassPool.classpath
            if (registeredCLs.containsKey(ucl)) {
                return registeredCLs.get(ucl);
            }
            ScopedClassPool pool = createScopedClassPool(ucl, classpool);
            registeredCLs.put(ucl, pool);
            return pool;
        }
    }

    /**
     * Get the registered classloaders.
     */
    @Override
    public Map<ClassLoader,ScopedClassPool> getRegisteredCLs() {
        clearUnregisteredClassLoaders();
        return registeredCLs;
    }

    /**
     * This method will check to see if a register classloader has been
     * undeployed (as in JBoss)
     */
    @Override
    public void clearUnregisteredClassLoaders() {
        List<ClassLoader> toUnregister = null;
        synchronized (registeredCLs) {
            for (Map.Entry<ClassLoader,ScopedClassPool> reg:registeredCLs.entrySet()) {
                if (reg.getValue().isUnloadedClassLoader()) {
                    ClassLoader cl = reg.getValue().getClassLoader();
                    if (cl != null) {
                        if (toUnregister == null)
                            toUnregister = new ArrayList<ClassLoader>();
                        toUnregister.add(cl);
                    }
                    registeredCLs.remove(reg.getKey());
                }
            }
            if (toUnregister != null)
                for (ClassLoader cl:toUnregister)
                    unregisterClassLoader(cl);
        }
    }

    @Override
    public void unregisterClassLoader(ClassLoader cl) {
        synchronized (registeredCLs) {
            ScopedClassPool pool = registeredCLs.remove(cl);
            if (pool != null)
                pool.close();
        }
    }

    public void insertDelegate(ScopedClassPoolRepository delegate) {
        // Noop - this is the end
    }

    @Override
    public void setClassPoolFactory(ScopedClassPoolFactory factory) {
        this.factory = factory;
    }

    @Override
    public ScopedClassPoolFactory getClassPoolFactory() {
        return factory;
    }
}
