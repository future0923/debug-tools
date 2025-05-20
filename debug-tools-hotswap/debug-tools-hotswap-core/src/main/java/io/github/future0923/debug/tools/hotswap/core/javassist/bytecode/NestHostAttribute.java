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
package io.github.future0923.debug.tools.hotswap.core.javassist.bytecode;


import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * <code>NestHost_attribute</code>.
 * It was introduced by JEP-181.  See JVMS 4.7.28 for the specification.
 *
 * @since 3.24
 */
public class NestHostAttribute extends AttributeInfo {
    /**
     * The name of this attribute <code>"NestHost"</code>.
     */
    public static final String tag = "NestHost";

    NestHostAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
        super(cp, n, in);
    }

    private NestHostAttribute(ConstPool cp, int hostIndex) {
        super(cp, tag, new byte[2]);
        ByteArray.write16bit(hostIndex, get(), 0);
    }

    /**
     * Makes a copy.  Class names are replaced according to the
     * given <code>Map</code> object.
     *
     * @param newCp     the constant pool table used by the new copy.
     * @param classnames        pairs of replaced and substituted
     *                          class names.
     */
    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
        int hostIndex = ByteArray.readU16bit(get(), 0);
        int newHostIndex = getConstPool().copy(hostIndex, newCp, classnames);
        return new NestHostAttribute(newCp, newHostIndex);
    }

    /**
     * Returns <code>host_class_index</code>.  The constant pool entry
     * at this entry is a <code>CONSTANT_Class_info</code> structure.
     * @return the value of <code>host_class_index</code>.
     */
    public int hostClassIndex() {
        return ByteArray.readU16bit(info, 0);
    }
}
