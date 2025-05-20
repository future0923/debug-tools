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
