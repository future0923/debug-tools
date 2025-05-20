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
package io.github.future0923.debug.tools.hotswap.core.javassist.convert;

import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeAttribute;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeIterator;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ConstPool;

final public class TransformNewClass extends Transformer {
    private int nested;
    private String classname, newClassName;
    private int newClassIndex, newMethodNTIndex, newMethodIndex;

    public TransformNewClass(Transformer next,
                             String classname, String newClassName) {
        super(next);
        this.classname = classname;
        this.newClassName = newClassName;
    }

    @Override
    public void initialize(ConstPool cp, CodeAttribute attr) {
        nested = 0;
        newClassIndex = newMethodNTIndex = newMethodIndex = 0;
    }

    /**
     * Modifies a sequence of
     *    NEW classname
     *    DUP
     *    ...
     *    INVOKESPECIAL classname:method
     */
    @Override
    public int transform(CtClass clazz, int pos, CodeIterator iterator,
                         ConstPool cp) throws CannotCompileException
    {
        int index;
        int c = iterator.byteAt(pos);
        if (c == NEW) {
            index = iterator.u16bitAt(pos + 1);
            if (cp.getClassInfo(index).equals(classname)) {
                if (iterator.byteAt(pos + 3) != DUP)
                    throw new CannotCompileException(
                                "NEW followed by no DUP was found");

                if (newClassIndex == 0)
                    newClassIndex = cp.addClassInfo(newClassName);

                iterator.write16bit(newClassIndex, pos + 1);
                ++nested;
            }
        }
        else if (c == INVOKESPECIAL) {
            index = iterator.u16bitAt(pos + 1);
            int typedesc = cp.isConstructor(classname, index);
            if (typedesc != 0 && nested > 0) {
                int nt = cp.getMethodrefNameAndType(index);
                if (newMethodNTIndex != nt) {
                    newMethodNTIndex = nt;
                    newMethodIndex = cp.addMethodrefInfo(newClassIndex, nt);
                }

                iterator.write16bit(newMethodIndex, pos + 1);
                --nested;
            }
        }

        return pos;
    }
}
