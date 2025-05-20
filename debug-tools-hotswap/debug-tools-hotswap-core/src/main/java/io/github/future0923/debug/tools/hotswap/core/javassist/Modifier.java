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

import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.AccessFlag;

/**
 * The Modifier class provides static methods and constants to decode
 * class and member access modifiers.  The constant values are equivalent
 * to the corresponding values in <code>javassist.bytecode.AccessFlag</code>.
 *
 * <p>All the methods/constants in this class are compatible with
 * ones in <code>java.lang.reflect.Modifier</code>.
 *
 * @see CtClass#getModifiers()
 */
public class Modifier {
    public static final int PUBLIC    = AccessFlag.PUBLIC;
    public static final int PRIVATE   = AccessFlag.PRIVATE;
    public static final int PROTECTED = AccessFlag.PROTECTED;
    public static final int STATIC    = AccessFlag.STATIC;
    public static final int FINAL     = AccessFlag.FINAL;
    public static final int SYNCHRONIZED = AccessFlag.SYNCHRONIZED;
    public static final int VOLATILE  = AccessFlag.VOLATILE;
    public static final int VARARGS = AccessFlag.VARARGS;
    public static final int TRANSIENT = AccessFlag.TRANSIENT;
    public static final int NATIVE    = AccessFlag.NATIVE;
    public static final int INTERFACE = AccessFlag.INTERFACE;
    public static final int ABSTRACT  = AccessFlag.ABSTRACT;
    public static final int STRICT    = AccessFlag.STRICT;
    public static final int ANNOTATION = AccessFlag.ANNOTATION;
    public static final int ENUM      = AccessFlag.ENUM;

    /**
     * Returns true if the modifiers include the <code>public</code>
     * modifier.
     */
    public static boolean isPublic(int mod) {
        return (mod & PUBLIC) != 0;
    }

    /**
     * Returns true if the modifiers include the <code>private</code>
     * modifier.
     */
    public static boolean isPrivate(int mod) {
        return (mod & PRIVATE) != 0;
    }

    /**
     * Returns true if the modifiers include the <code>protected</code>
     * modifier.
     */
    public static boolean isProtected(int mod) {
        return (mod & PROTECTED) != 0;
    }

    /**
     * Returns true if the modifiers do not include either
     * <code>public</code>, <code>protected</code>, or <code>private</code>.
     */
    public static boolean isPackage(int mod) {
        return (mod & (PUBLIC | PRIVATE | PROTECTED)) == 0;
    }

    /**
     * Returns true if the modifiers include the <code>static</code>
     * modifier.
     */
    public static boolean isStatic(int mod) {
        return (mod & STATIC) != 0;
    }

    /**
     * Returns true if the modifiers include the <code>final</code>
     * modifier.
     */
    public static boolean isFinal(int mod) {
        return (mod & FINAL) != 0;
    }

    /**
     * Returns true if the modifiers include the <code>synchronized</code>
     * modifier.
     */
    public static boolean isSynchronized(int mod) {
        return (mod & SYNCHRONIZED) != 0;
    }

    /**
     * Returns true if the modifiers include the <code>volatile</code>
     * modifier.
     */
    public static boolean isVolatile(int mod) {
        return (mod & VOLATILE) != 0;
    }

    /**
     * Returns true if the modifiers include the <code>transient</code>
     * modifier.
     */
    public static boolean isTransient(int mod) {
        return (mod & TRANSIENT) != 0;
    }

    /**
     * Returns true if the modifiers include the <code>native</code>
     * modifier.
     */
    public static boolean isNative(int mod) {
        return (mod & NATIVE) != 0;
    }

    /**
     * Returns true if the modifiers include the <code>interface</code>
     * modifier.
     */
    public static boolean isInterface(int mod) {
        return (mod & INTERFACE) != 0;
    }

    /**
     * Returns true if the modifiers include the <code>annotation</code>
     * modifier.
     *
     * @since 3.2
     */
    public static boolean isAnnotation(int mod) {
        return (mod & ANNOTATION) != 0;
    }

    /**
     * Returns true if the modifiers include the <code>enum</code>
     * modifier.
     *
     * @since 3.2
     */
    public static boolean isEnum(int mod) {
        return (mod & ENUM) != 0;
    }

    /**
     * Returns true if the modifiers include the <code>abstract</code>
     * modifier.
     */
    public static boolean isAbstract(int mod) {
        return (mod & ABSTRACT) != 0;
    }

    /**
     * Returns true if the modifiers include the <code>strictfp</code>
     * modifier.
     */
    public static boolean isStrict(int mod) {
        return (mod & STRICT) != 0;
    }

    /**
     * Returns true if the modifiers include the <code>varargs</code>
     * (variable number of arguments) modifier.
     */
    public static boolean isVarArgs(int mod)  {
        return (mod & VARARGS) != 0;
    }

    /**
     * Truns the public bit on.  The protected and private bits are
     * cleared.
     */
    public static int setPublic(int mod) {
        return (mod & ~(PRIVATE | PROTECTED)) | PUBLIC;
    }

    /**
     * Truns the protected bit on.  The protected and public bits are
     * cleared.
     */
    public static int setProtected(int mod) {
        return (mod & ~(PRIVATE | PUBLIC)) | PROTECTED;
    }

    /**
     * Truns the private bit on.  The protected and private bits are
     * cleared.
     */
    public static int setPrivate(int mod) {
        return (mod & ~(PROTECTED | PUBLIC)) | PRIVATE;
    }

    /**
     * Clears the public, protected, and private bits.
     */
    public static int setPackage(int mod) {
        return (mod & ~(PROTECTED | PUBLIC | PRIVATE));
    }

    /**
     * Clears a specified bit in <code>mod</code>.
     */
    public static int clear(int mod, int clearBit) {
        return mod & ~clearBit;
    }

    /**
     * Return a string describing the access modifier flags in
     * the specified modifier.
     *
     * @param mod   modifier flags.
     */
    public static String toString(int mod) {
        return java.lang.reflect.Modifier.toString(mod);
    }
}
