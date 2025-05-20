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
package io.github.future0923.debug.tools.hotswap.core.javassist;


import java.io.InputStream;
import java.net.URL;

/**
 * A search-path for obtaining a class file
 * by <code>getResourceAsStream()</code> in <code>java.lang.Class</code>.
 *
 * <p>Try adding a <code>ClassClassPath</code> when a program is running
 * with a user-defined class loader and any class files are not found with
 * the default <code>ClassPool</code>.  For example,
 *
 * <pre>
 * ClassPool cp = ClassPool.getDefault();
 * cp.insertClassPath(new ClassClassPath(this.getClass()));
 * </pre>
 *
 * This code snippet permanently adds a <code>ClassClassPath</code>
 * to the default <code>ClassPool</code>.  Note that the default
 * <code>ClassPool</code> is a singleton.  The added
 * <code>ClassClassPath</code> uses a class object representing
 * the class including the code snippet above.
 *
 * <p>Class files in a named module are private to that module.
 * This method cannot obtain class files in named modules.
 * </p>
 *
 * @see ClassPool#insertClassPath(ClassPath)
 * @see ClassPool#appendClassPath(ClassPath)
 * @see LoaderClassPath
 */
public class ClassClassPath implements ClassPath {
    private Class<?> thisClass;

    /** Creates a search path.
     *
     * @param c     the <code>Class</code> object used to obtain a class
     *              file.  <code>getResourceAsStream()</code> is called on
     *              this object.
     */
    public ClassClassPath(Class<?> c) {
        thisClass = c;
    }

    ClassClassPath() {
        /* The value of thisClass was this.getClass() in early versions:
         *
         *     thisClass = this.getClass();
         *
         * However, this made openClassfile() not search all the system
         * class paths if javassist.jar is put in jre/lib/ext/
         * (with JDK1.4).
         */
        this(Object.class);
    }

    /**
     * Obtains a class file by <code>getResourceAsStream()</code>.
     */
    @Override
    public InputStream openClassfile(String classname) throws NotFoundException {
        String filename = '/' + classname.replace('.', '/') + ".class";
        return thisClass.getResourceAsStream(filename);
    }

    /**
     * Obtains the URL of the specified class file.
     *
     * @return null if the class file could not be found.
     */
    @Override
    public URL find(String classname) {
        String filename = '/' + classname.replace('.', '/') + ".class";
        return thisClass.getResource(filename);
    }

    @Override
    public String toString() {
        return thisClass.getName() + ".class";
    }
}
