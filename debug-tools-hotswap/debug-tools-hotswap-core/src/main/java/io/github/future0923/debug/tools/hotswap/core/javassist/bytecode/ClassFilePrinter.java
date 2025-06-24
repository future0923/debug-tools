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
package io.github.future0923.debug.tools.hotswap.core.javassist.bytecode;

import java.io.PrintWriter;
import java.util.List;

import io.github.future0923.debug.tools.hotswap.core.javassist.Modifier;

/**
 * A utility class for priting the contents of a class file.
 * It prints a constant pool table, fields, and methods in a
 * human readable representation.
 */
public class ClassFilePrinter {
    /**
     * Prints the contents of a class file to the standard output stream.
     */
    public static void print(ClassFile cf) {
        print(cf, new PrintWriter(System.out, true));
    }

    /**
     * Prints the contents of a class file.
     */
    public static void print(ClassFile cf, PrintWriter out) {
        /* 0x0020 (SYNCHRONIZED) means ACC_SUPER if the modifiers
         * are of a class.
         */
        int mod
            = AccessFlag.toModifier(cf.getAccessFlags()
                                    & ~AccessFlag.SYNCHRONIZED);
        out.println("major: " + cf.major + ", minor: " + cf.minor
                    + " modifiers: " + Integer.toHexString(cf.getAccessFlags()));
        out.println(Modifier.toString(mod) + " class "
                    + cf.getName() + " extends " + cf.getSuperclass());

        String[] infs = cf.getInterfaces();
        if (infs != null && infs.length > 0) {
            out.print("    implements ");
            out.print(infs[0]);
            for (int i = 1; i < infs.length; ++i)
                out.print(", " + infs[i]);

            out.println();
        }

        out.println();
        List<FieldInfo> fields = cf.getFields();
        for (FieldInfo finfo:fields) {
            int acc = finfo.getAccessFlags();
            out.println(Modifier.toString(AccessFlag.toModifier(acc))
                        + " " + finfo.getName() + "\t"
                        + finfo.getDescriptor());
            printAttributes(finfo.getAttributes(), out, 'f');
        }

        out.println();
        List<MethodInfo> methods = cf.getMethods();
        for (MethodInfo minfo:methods) {
            int acc = minfo.getAccessFlags();
            out.println(Modifier.toString(AccessFlag.toModifier(acc))
                        + " " + minfo.getName() + "\t"
                        + minfo.getDescriptor());
            printAttributes(minfo.getAttributes(), out, 'm');
            out.println();
        }

        out.println();
        printAttributes(cf.getAttributes(), out, 'c');
    }

    static void printAttributes(List<AttributeInfo> list, PrintWriter out, char kind) {
        if (list == null)
            return;

        for (AttributeInfo ai:list) {
            if (ai instanceof CodeAttribute) {
                CodeAttribute ca = (CodeAttribute)ai;
                out.println("attribute: " + ai.getName() + ": "
                            + ai.getClass().getName());
                out.println("max stack " + ca.getMaxStack()
                            + ", max locals " + ca.getMaxLocals()
                            + ", " + ca.getExceptionTable().size()
                            + " catch blocks");
                out.println("<code attribute begin>");
                printAttributes(ca.getAttributes(), out, kind);
                out.println("<code attribute end>");
            }
            else if (ai instanceof AnnotationsAttribute) {
                out.println("annnotation: " + ai.toString());
            }
            else if (ai instanceof ParameterAnnotationsAttribute) {
                out.println("parameter annnotations: " + ai.toString());
            }
            else if (ai instanceof StackMapTable) {
                out.println("<stack map table begin>");
                StackMapTable.Printer.print((StackMapTable)ai, out);
                out.println("<stack map table end>");
            }
            else if (ai instanceof StackMap) {
                out.println("<stack map begin>");
                ((StackMap)ai).print(out);
                out.println("<stack map end>");
            }
            else if (ai instanceof SignatureAttribute) {
                SignatureAttribute sa = (SignatureAttribute)ai;
                String sig = sa.getSignature();
                out.println("signature: " + sig);
                try {
                    String s;
                    if (kind == 'c')
                        s = SignatureAttribute.toClassSignature(sig).toString();
                    else if (kind == 'm')
                        s = SignatureAttribute.toMethodSignature(sig).toString();
                    else
                        s = SignatureAttribute.toFieldSignature(sig).toString();

                    out.println("           " + s);
                }
                catch (BadBytecode e) {
                    out.println("           syntax error");
                }
            }
            else
                out.println("attribute: " + ai.getName()
                            + " (" + ai.get().length + " byte): "
                            + ai.getClass().getName());
        }
    }
}
