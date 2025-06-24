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
package io.github.future0923.debug.tools.hotswap.core.javassist;


import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * A class search-path representing a class loader.
 *
 * <p>It is used for obtaining a class file from the given
 * class loader by <code>getResourceAsStream()</code>.
 * The <code>LoaderClassPath</code> refers to the class loader through
 * <code>WeakReference</code>.  If the class loader is garbage collected,
 * the other search pathes are examined.
 *
 * <p>The given class loader must have both <code>getResourceAsStream()</code>
 * and <code>getResource()</code>.
 * 
 * <p>Class files in a named module are private to that module.
 * This method cannot obtain class files in named modules.
 * </p>
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @author Shigeru Chiba
 *
 * @see ClassPool#insertClassPath(ClassPath)
 * @see ClassPool#appendClassPath(ClassPath)
 * @see ClassClassPath
 */
public class LoaderClassPath implements ClassPath {
    private Reference<ClassLoader> clref;

    /**
     * Creates a search path representing a class loader.
     */
    public LoaderClassPath(ClassLoader cl) {
        clref = new WeakReference<ClassLoader>(cl);
    }

    @Override
    public String toString() {
        return clref.get() == null ? "<null>" : clref.get().toString();
    }

    /**
     * Obtains a class file from the class loader.
     * This method calls <code>getResourceAsStream(String)</code>
     * on the class loader.
     */
    @Override
    public InputStream openClassfile(String classname) throws NotFoundException {
        String cname = classname.replace('.', '/') + ".class";
        ClassLoader cl = clref.get();
        if (cl == null)
            return null;        // not found
        InputStream is = cl.getResourceAsStream(cname);
        return is;
    }

    /**
     * Obtains the URL of the specified class file.
     * This method calls <code>getResource(String)</code>
     * on the class loader.
     *
     * @return null if the class file could not be found. 
     */
    @Override
    public URL find(String classname) {
        String cname = classname.replace('.', '/') + ".class";
        ClassLoader cl = clref.get();
        if (cl == null)
            return null;        // not found
        URL url = cl.getResource(cname);
        return url;
    }
}
