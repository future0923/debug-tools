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
package io.github.future0923.debug.tools.hotswap.core.javassist.util.proxy;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ClassFile;

/**
 * Helper class for invoking {@link ClassLoader#defineClass(String,byte[],int,int)}.
 *
 * @since 3.22
 */
public class DefinePackageHelper
{
    private static abstract class Helper {
        abstract Package definePackage(ClassLoader loader, String name, String specTitle,
                String specVersion, String specVendor, String implTitle, String implVersion,
                String implVendor, URL sealBase)
            throws IllegalArgumentException;
    }

    private static class Java9 extends Helper {
        // definePackage has been discontinued for JAVA 9
        @Override
        Package definePackage(ClassLoader loader, String name, String specTitle,
                    String specVersion, String specVendor, String implTitle, String implVersion,
                    String implVendor, URL sealBase)
            throws IllegalArgumentException
        {
            throw new RuntimeException("define package has been disabled for jigsaw");
        }
    };

    private static class Java7 extends Helper {
        private final SecurityActions stack = SecurityActions.stack;
        private final MethodHandle definePackage = getDefinePackageMethodHandle();

        private MethodHandle getDefinePackageMethodHandle() {
            if (stack.getCallerClass() != this.getClass())
                throw new IllegalAccessError("Access denied for caller.");
            try {
                return SecurityActions.getMethodHandle(ClassLoader.class, 
                            "definePackage", new Class[] {
                                String.class, String.class, String.class, String.class,
                                String.class, String.class, String.class, URL.class 
                            });
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("cannot initialize", e);
            }
        }
            
        @Override
        Package definePackage(ClassLoader loader, String name, String specTitle,
                    String specVersion, String specVendor, String implTitle, String implVersion,
                    String implVendor, URL sealBase)
            throws IllegalArgumentException
        {
            if (stack.getCallerClass() != DefinePackageHelper.class)
                throw new IllegalAccessError("Access denied for caller.");
            try {
                return (Package) definePackage.invokeWithArguments(loader, name, specTitle,
                        specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
            } catch (Throwable e) {
                if (e instanceof IllegalArgumentException) throw (IllegalArgumentException) e;
                if (e instanceof RuntimeException) throw (RuntimeException) e;
            }
            return null;
        }
    }

    private static class JavaOther extends Helper {
        private final SecurityActions stack = SecurityActions.stack;
        private final Method definePackage = getDefinePackageMethod();

        private Method getDefinePackageMethod() {
            if (stack.getCallerClass() != this.getClass())
                throw new IllegalAccessError("Access denied for caller.");
            try {
                return SecurityActions.getDeclaredMethod(ClassLoader.class, 
                            "definePackage", new Class[] {
                                String.class, String.class, String.class, String.class,
                                String.class, String.class, String.class, URL.class 
                            });
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("cannot initialize", e);
            }
        }
            
        @Override
        Package definePackage(ClassLoader loader, String name, String specTitle,
                    String specVersion, String specVendor, String implTitle, String implVersion,
                    String implVendor, URL sealBase)
            throws IllegalArgumentException
        {
            if (stack.getCallerClass() != DefinePackageHelper.class)
                throw new IllegalAccessError("Access denied for caller.");
            try {
                definePackage.setAccessible(true);
                return (Package) definePackage.invoke(loader, new Object[] {
                        name, specTitle, specVersion, specVendor, implTitle,
                        implVersion, implVendor, sealBase                                
                    });
            } catch (Throwable e) {
                if (e instanceof InvocationTargetException) {
                    Throwable t = ((InvocationTargetException) e).getTargetException();
                    if (t instanceof IllegalArgumentException)
                        throw (IllegalArgumentException) t;
                }
                if (e instanceof RuntimeException) throw (RuntimeException) e;
            }
            finally {
                definePackage.setAccessible(false);
            }
            return null;
        }
    };

    private static final Helper privileged
        = ClassFile.MAJOR_VERSION >= ClassFile.JAVA_9
          ? new Java9() : ClassFile.MAJOR_VERSION >= ClassFile.JAVA_7
                          ? new Java7() : new JavaOther();

    /**
     * Defines a new package.  If the package is already defined, this method
     * performs nothing.
     *
     * <p>You do not necessarily need to
     * call this method.  If this method is called, then  
     * <code>getPackage()</code> on the <code>Class</code> object returned 
     * by <code>toClass()</code> will return a non-null object.</p>
     *
     * <p>The jigsaw module introduced by Java 9 has broken this method.
     * In Java 9 or later, the VM argument
     * <code>--add-opens java.base/java.lang=ALL-UNNAMED</code>
     * has to be given to the JVM so that this method can run.
     * </p>
     *
     * @param loader        the class loader passed to <code>toClass()</code> or
     *                      the default one obtained by <code>getClassLoader()</code>.
     * @param className     the package name.
     * @see Class#getClassLoader()
     * @see CtClass#toClass()
     */
    public static void definePackage(String className, ClassLoader loader)
        throws CannotCompileException
    {
        try {
            privileged.definePackage(loader, className, 
                    null, null, null, null, null, null, null);
        }
        catch (IllegalArgumentException e) {
            // if the package is already defined, an IllegalArgumentException
            // is thrown.
            return;
        }
        catch (Exception e) {
            throw new CannotCompileException(e);
        }
    }
    
    private DefinePackageHelper() {}
}
