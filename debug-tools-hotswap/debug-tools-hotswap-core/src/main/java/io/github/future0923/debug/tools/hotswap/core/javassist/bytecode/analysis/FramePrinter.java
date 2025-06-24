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
package io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.analysis;

import java.io.PrintStream;

import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.Modifier;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.BadBytecode;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeAttribute;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeIterator;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ConstPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.Descriptor;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.InstructionPrinter;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.MethodInfo;

/**
 * A utility class for printing a merged view of the frame state and the
 * instructions of a method.
 *
 * @author Jason T. Greene
 */
public final class FramePrinter {
    private final PrintStream stream;

    /**
     * Constructs a bytecode printer.
     */
    public FramePrinter(PrintStream stream) {
        this.stream = stream;
    }

    /**
     * Prints all the methods declared in the given class. 
     */
    public static void print(CtClass clazz, PrintStream stream) {
        (new FramePrinter(stream)).print(clazz);
    }

    /**
     * Prints all the methods declared in the given class. 
     */
    public void print(CtClass clazz) {
        CtMethod[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            print(methods[i]);
        }
    }

    private String getMethodString(CtMethod method) {
        try {
            return Modifier.toString(method.getModifiers()) + " "
                    + method.getReturnType().getName() + " " + method.getName()
                    + Descriptor.toString(method.getSignature()) + ";";
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prints the instructions and the frame states of the given method.
     */
    public void print(CtMethod method) {
        stream.println("\n" + getMethodString(method));
        MethodInfo info = method.getMethodInfo2();
        ConstPool pool = info.getConstPool();
        CodeAttribute code = info.getCodeAttribute();
        if (code == null)
            return;

        Frame[] frames;
        try {
            frames = (new Analyzer()).analyze(method.getDeclaringClass(), info);
        } catch (BadBytecode e) {
            throw new RuntimeException(e);
        }

        int spacing = String.valueOf(code.getCodeLength()).length();

        CodeIterator iterator = code.iterator();
        while (iterator.hasNext()) {
            int pos;
            try {
                pos = iterator.next();
            } catch (BadBytecode e) {
                throw new RuntimeException(e);
            }

            stream.println(pos + ": " + InstructionPrinter.instructionString(iterator, pos, pool));

            addSpacing(spacing + 3);
            Frame frame = frames[pos];
            if (frame == null) {
                stream.println("--DEAD CODE--");
                continue;
            }
            printStack(frame);

            addSpacing(spacing + 3);
            printLocals(frame);
        }

    }

    private void printStack(Frame frame) {
        stream.print("stack [");
        int top = frame.getTopIndex();
        for (int i = 0; i <= top; i++) {
            if (i > 0)
                stream.print(", ");
            Type type = frame.getStack(i);
            stream.print(type);
        }
        stream.println("]");
    }

    private void printLocals(Frame frame) {
        stream.print("locals [");
        int length = frame.localsLength();
        for (int i = 0; i < length; i++) {
            if (i > 0)
                stream.print(", ");
            Type type = frame.getLocal(i);
            stream.print(type == null ? "empty" : type.toString());
        }
        stream.println("]");
    }

    private void addSpacing(int count) {
        while (count-- > 0)
            stream.print(' ');
    }
}
