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
package io.github.future0923.debug.tools.hotswap.core.javassist.tools.reflect;

import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;

/**
 * A class loader for reflection.
 *
 * <p>To run a program, say <code>MyApp</code>,
 * including a reflective class,
 * you must write a start-up program as follows:
 *
 * <pre>
 * public class Main {
 *   public static void main(String[] args) throws Throwable {
 *     javassist.tools.reflect.Loader cl
 *         = (javassist.tools.reflect.Loader)Main.class.getClassLoader();
 *     cl.makeReflective("Person", "MyMetaobject",
 *                       "io.github.future0923.debug.tools.hotswap.core.javassist.tools.reflect.ClassMetaobject");
 *     cl.run("MyApp", args);
 *   }
 * }
 * </pre>
 *
 * <p>Then run this program as follows:
 *
 * <pre>% java javassist.tools.reflect.Loader Main arg1, ...</pre>
 *
 * <p>This command runs <code>Main.main()</code> with <code>arg1</code>, ...
 * and <code>Main.main()</code> runs <code>MyApp.main()</code> with
 * <code>arg1</code>, ...
 * The <code>Person</code> class is modified
 * to be a reflective class.  Method calls on a <code>Person</code>
 * object are intercepted by an instance of <code>MyMetaobject</code>.
 *
 * <p>Also, you can run <code>MyApp</code> in a slightly different way:
 *
 * <pre>
 * public class Main2 {
 *   public static void main(String[] args) throws Throwable {
 *     javassist.tools.reflect.Loader cl = new javassist.tools.reflect.Loader();
 *     cl.makeReflective("Person", "MyMetaobject",
 *                       "io.github.future0923.debug.tools.hotswap.core.javassist.tools.reflect.ClassMetaobject");
 *     cl.run("MyApp", args);
 *   }
 * }
 * </pre>
 *
 * <p>This program is run as follows:
 *
 * <pre>% java Main2 arg1, ...</pre>
 *
 * <p>The difference from the former one is that the class <code>Main</code>
 * is loaded by <code>javassist.tools.reflect.Loader</code> whereas the class
 * <code>Main2</code> is not.  Thus, <code>Main</code> belongs
 * to the same name space (security domain) as <code>MyApp</code>
 * whereas <code>Main2</code> does not; <code>Main2</code> belongs
 * to the same name space as <code>javassist.tools.reflect.Loader</code>.
 * For more details,
 * see the notes in the manual page of <code>javassist.Loader</code>.
 *
 * <p>The class <code>Main2</code> is equivalent to this class:
 *
 * <pre>
 * public class Main3 {
 *   public static void main(String[] args) throws Throwable {
 *     Reflection reflection = new Reflection();
 *     javassist.Loader cl
 *         = new javassist.Loader(ClassPool.getDefault(reflection));
 *     reflection.makeReflective("Person", "MyMetaobject",
 *                               "io.github.future0923.debug.tools.hotswap.core.javassist.tools.reflect.ClassMetaobject");
 *     cl.run("MyApp", args);
 *   }
 * }
 * </pre>
 *
 * <p><b>Note:</b>
 *
 * <p><code>javassist.tools.reflect.Loader</code> does not make a class reflective
 * if that class is in a <code>java.*</code> or
 * <code>javax.*</code> pacakge because of the specifications
 * on the class loading algorithm of Java.  The JVM does not allow to
 * load such a system class with a user class loader.
 *
 * <p>To avoid this limitation, those classes should be statically
 * modified with <code>javassist.tools.reflect.Compiler</code> and the original
 * class files should be replaced.
 *
 * @see javassist.tools.reflect.Reflection
 * @see javassist.tools.reflect.Compiler
 * @see javassist.Loader
 */
public class Loader extends io.github.future0923.debug.tools.hotswap.core.javassist.Loader {
    protected Reflection reflection;

    /**
     * Loads a class with an instance of <code>Loader</code>
     * and calls <code>main()</code> in that class.
     *
     * @param args              command line parameters.
     * <br>&nbsp;&nbsp;<code>args[0]</code> is the class name to be loaded.
     * <br>&nbsp;&nbsp;<code>args[1..n]</code> are parameters passed
     *                      to the target <code>main()</code>.
     */
    public static void main(String[] args) throws Throwable {
        Loader cl = new Loader();
        cl.run(args);
    }

    /**
     * Constructs a new class loader.
     */
    public Loader() throws CannotCompileException, NotFoundException {
        super();
        delegateLoadingOf("io.github.future0923.debug.tools.hotswap.core.javassist.tools.reflect.Loader");

        reflection = new Reflection();
        ClassPool pool = ClassPool.getDefault();
        addTranslator(pool, reflection);
    }

    /**
     * Produces a reflective class.
     * If the super class is also made reflective, it must be done
     * before the sub class.
     *
     * @param clazz             the reflective class.
     * @param metaobject        the class of metaobjects.
     *                          It must be a subclass of
     *                          <code>Metaobject</code>.
     * @param metaclass         the class of the class metaobject.
     *                          It must be a subclass of
     *                          <code>ClassMetaobject</code>.
     * @return <code>false</code>       if the class is already reflective.
     *
     * @see javassist.tools.reflect.Metaobject
     * @see javassist.tools.reflect.ClassMetaobject
     */
    public boolean makeReflective(String clazz,
                                  String metaobject, String metaclass)
        throws CannotCompileException, NotFoundException
    {
        return reflection.makeReflective(clazz, metaobject, metaclass);
    }
}
