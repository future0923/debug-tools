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
package io.github.future0923.debug.tools.hotswap.core.javassist.tools;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;

import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ClassFile;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ClassFilePrinter;

/**
 * Dump is a tool for viewing the class definition in the given
 * class file.  Unlike the JDK javap tool, Dump works even if
 * the class file is broken.
 *
 * <p>For example,
 * <pre>% java javassist.tools.Dump foo.class</pre>
 *
 * <p>prints the contents of the constant pool and the list of methods
 * and fields.
 */
public class Dump {
    private Dump() {}

    /**
     * Main method.
     *
     * @param args           <code>args[0]</code> is the class file name.
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java Dump <class file name>");
            return;
        }

        DataInputStream in = new DataInputStream(
                                         new FileInputStream(args[0]));
        ClassFile w = new ClassFile(in);
        PrintWriter out = new PrintWriter(System.out, true);
        out.println("*** constant pool ***");
        w.getConstPool().print(out);
        out.println();
        out.println("*** members ***");
        ClassFilePrinter.print(w, out);
    }
}
