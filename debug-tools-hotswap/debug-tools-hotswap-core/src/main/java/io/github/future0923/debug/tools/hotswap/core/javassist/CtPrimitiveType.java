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


/**
 * An instance of <code>CtPrimitiveType</code> represents a primitive type.
 * It is obtained from <code>CtClass</code>.
 */
public final class CtPrimitiveType extends CtClass {
    private char descriptor;
    private String wrapperName;
    private String getMethodName;
    private String mDescriptor;
    private int returnOp;
    private int arrayType;
    private int dataSize;

    CtPrimitiveType(String name, char desc, String wrapper,
                    String methodName, String mDesc, int opcode, int atype,
                    int size) {
        super(name);
        descriptor = desc;
        wrapperName = wrapper;
        getMethodName = methodName;
        mDescriptor = mDesc;
        returnOp = opcode;
        arrayType = atype;
        dataSize = size;
    }

    /**
     * Returns <code>true</code> if this object represents a primitive
     * Java type: boolean, byte, char, short, int, long, float, double,
     * or void.
     */
    @Override
    public boolean isPrimitive() { return true; }

    /**
     * Returns the modifiers for this type.
     * For decoding, use <code>javassist.Modifier</code>.
     *
     * @see Modifier
     */
    @Override
    public int getModifiers() {
        return Modifier.PUBLIC | Modifier.FINAL;
    }

    /**
     * Returns the descriptor representing this type.
     * For example, if the type is int, then the descriptor is I.
     */
    public char getDescriptor() { return descriptor; }

    /**
     * Returns the name of the wrapper class.
     * For example, if the type is int, then the wrapper class is
     * <code>java.lang.Integer</code>.
     */
    public String getWrapperName() { return wrapperName; }

    /**
     * Returns the name of the method for retrieving the value
     * from the wrapper object.
     * For example, if the type is int, then the method name is
     * <code>intValue</code>.
     */
    public String getGetMethodName() { return getMethodName; }

    /**
     * Returns the descriptor of the method for retrieving the value
     * from the wrapper object.
     * For example, if the type is int, then the method descriptor is
     * <code>()I</code>.
     */
    public String getGetMethodDescriptor() { return mDescriptor; }

    /**
     * Returns the opcode for returning a value of the type.
     * For example, if the type is int, then the returned opcode is
     * <code>javassit.bytecode.Opcode.IRETURN</code>.
     */
    public int getReturnOp() { return returnOp; }

    /**
     * Returns the array-type code representing the type.
     * It is used for the newarray instruction.
     * For example, if the type is int, then this method returns
     * <code>javassit.bytecode.Opcode.T_INT</code>.
     */
    public int getArrayType() { return arrayType; }

    /**
     * Returns the data size of the primitive type.
     * If the type is long or double, this method returns 2.
     * Otherwise, it returns 1.
     */
    public int getDataSize() { return dataSize; }
}
