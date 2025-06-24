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
package io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.annotation;

import java.io.IOException;
import java.io.OutputStream;

import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ConstPool;

/**
 * A convenience class for constructing a
 * {@code ..TypeAnnotations_attribute}.
 * See the source code of the {@link javassist.bytecode.TypeAnnotationsAttribute} class.
 *
 * @since 3.19
 */
public class TypeAnnotationsWriter extends AnnotationsWriter {
    /**
     * Constructs with the given output stream.
     *
     * @param os    the output stream.
     * @param cp    the constant pool.
     */
    public TypeAnnotationsWriter(OutputStream os, ConstPool cp) {
        super(os, cp);
    }

    /**
     * Writes {@code num_annotations} in
     * {@code Runtime(In)VisibleTypeAnnotations_attribute}.
     * It must be followed by {@code num} instances of {@code type_annotation}.
     */
    @Override
    public void numAnnotations(int num) throws IOException {
        super.numAnnotations(num);
    }

    /**
     * Writes {@code target_type} and {@code type_parameter_target}
     * of {@code target_info} union.
     */
    public void typeParameterTarget(int targetType, int typeParameterIndex)
        throws IOException
    {
        output.write(targetType);
        output.write(typeParameterIndex);
    }

    /**
     * Writes {@code target_type} and {@code supertype_target}
     * of {@code target_info} union.
     */
    public void supertypeTarget(int supertypeIndex)
        throws IOException
    {
        output.write(0x10);
        write16bit(supertypeIndex);
    }    

    /**
     * Writes {@code target_type} and {@code type_parameter_bound_target}
     * of {@code target_info} union.
     */
    public void typeParameterBoundTarget(int targetType, int typeParameterIndex, int boundIndex)
        throws IOException
    {
        output.write(targetType);
        output.write(typeParameterIndex);
        output.write(boundIndex);
    }

    /**
     * Writes {@code target_type} and {@code empty_target}
     * of {@code target_info} union.
     */
    public void emptyTarget(int targetType) throws IOException {
        output.write(targetType);
    }

    /**
     * Writes {@code target_type} and {@code type_parameter_target}
     * of {@code target_info} union.
     */
    public void formalParameterTarget(int formalParameterIndex)
        throws IOException
    {
        output.write(0x16);
        output.write(formalParameterIndex);
    }

    /**
     * Writes {@code target_type} and {@code throws_target}
     * of {@code target_info} union.
     */
    public void throwsTarget(int throwsTypeIndex)
        throws IOException
    {
        output.write(0x17);
        write16bit(throwsTypeIndex);
    } 

    /**
     * Writes {@code target_type} and {@code localvar_target}
     * of {@code target_info} union.
     * It must be followed by {@code tableLength} calls
     * to {@code localVarTargetTable}.
     */
    public void localVarTarget(int targetType, int tableLength)
        throws IOException
    {
        output.write(targetType);
        write16bit(tableLength);
    }

    /**
     * Writes an element of {@code table[]} of {@code localvar_target}
     * of {@code target_info} union.
     */
    public void localVarTargetTable(int startPc, int length, int index)
        throws IOException
    {
        write16bit(startPc);
        write16bit(length);
        write16bit(index);
    }

    /**
     * Writes {@code target_type} and {@code catch_target}
     * of {@code target_info} union.
     */
    public void catchTarget(int exceptionTableIndex)
        throws IOException
    {
        output.write(0x42);
        write16bit(exceptionTableIndex);
    } 

    /**
     * Writes {@code target_type} and {@code offset_target}
     * of {@code target_info} union.
     */
    public void offsetTarget(int targetType, int offset)
        throws IOException
    {
        output.write(targetType);
        write16bit(offset);
    }

    /**
     * Writes {@code target_type} and {@code type_argument_target}
     * of {@code target_info} union.
     */
    public void typeArgumentTarget(int targetType, int offset, int type_argument_index)
        throws IOException
    {
        output.write(targetType);
        write16bit(offset);
        output.write(type_argument_index);
    }

    /**
     * Writes {@code path_length} of {@code type_path}.
     */
    public void typePath(int pathLength) throws IOException {
        output.write(pathLength);
    }

    /**
     * Writes an element of {@code path[]} of {@code type_path}. 
     */
    public void typePathPath(int typePathKind, int typeArgumentIndex)
        throws IOException
    {
        output.write(typePathKind);
        output.write(typeArgumentIndex);
    }
}
