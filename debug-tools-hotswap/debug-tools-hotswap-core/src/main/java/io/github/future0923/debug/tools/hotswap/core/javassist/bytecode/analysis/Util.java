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

import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeIterator;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.Opcode;

/**
 * A set of common utility methods.
 *
 * @author Jason T. Greene
 */
public class Util implements Opcode {
    public static int getJumpTarget(int pos, CodeIterator iter) {
        int opcode = iter.byteAt(pos);
        pos += (opcode == JSR_W || opcode == GOTO_W) ? iter.s32bitAt(pos + 1) : iter.s16bitAt(pos + 1);
        return pos;
    }

    public static boolean isJumpInstruction(int opcode) {
        return (opcode >= IFEQ && opcode <= JSR) || opcode == IFNULL || opcode == IFNONNULL || opcode == JSR_W || opcode == GOTO_W;
    }

    public static boolean isGoto(int opcode) {
        return opcode == GOTO || opcode == GOTO_W;
    }

    public static boolean isJsr(int opcode) {
        return opcode == JSR || opcode == JSR_W;
    }

    public static boolean isReturn(int opcode) {
        return (opcode >= IRETURN && opcode <= RETURN);
    }
}
