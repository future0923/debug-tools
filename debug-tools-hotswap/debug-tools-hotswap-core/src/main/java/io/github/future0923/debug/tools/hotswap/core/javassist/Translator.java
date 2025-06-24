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


/**
 * An observer of <code>Loader</code>.
 * The users can define a class implementing this
 * interface and attach an instance of that class to a
 * <code>Loader</code> object so that it can translate a class file
 * when the class file is loaded into the JVM.
 *
 * @see Loader#addTranslator(ClassPool, Translator)
 */
public interface Translator {
    /**
     * Is invoked by a <code>Loader</code> for initialization
     * when the object is attached to the <code>Loader</code> object.
     * This method can be used for getting (for caching) some
     * <code>CtClass</code> objects that will be accessed
     * in <code>onLoad()</code> in <code>Translator</code>.
     *
     * @param pool      the <code>ClassPool</code> that this translator
     *                          should use.
     * @see Loader
     * @throws NotFoundException    if a <code>CtClass</code> cannot be found.
     * @throws CannotCompileException   if the initialization by this method
     *                                  fails.
     */
    void start(ClassPool pool)
        throws NotFoundException, CannotCompileException;

    /**
     * Is invoked by a <code>Loader</code> for notifying that
     * a class is loaded.  The <code>Loader</code> calls
     *
     * <pre>
     * pool.get(classname).toBytecode()</pre>
     *
     * to read the class file after <code>onLoad()</code> returns.
     *
     * <p><code>classname</code> may be the name of a class
     * that has not been created yet.
     * If so, <code>onLoad()</code> must create that class so that
     * the <code>Loader</code> can read it after <code>onLoad()</code>
     * returns.
     *
     * @param pool      the <code>ClassPool</code> that this translator
     *                          should use.
     * @param classname     the name of the class being loaded.
     * @see Loader
     * @throws NotFoundException    if a <code>CtClass</code> cannot be found.
     * @throws CannotCompileException   if the code transformation
     *                                  by this method fails.
     */
    void onLoad(ClassPool pool, String classname)
        throws NotFoundException, CannotCompileException;
}
