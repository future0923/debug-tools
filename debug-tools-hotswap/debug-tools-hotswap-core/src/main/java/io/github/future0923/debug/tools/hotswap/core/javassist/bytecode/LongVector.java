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


final class LongVector {
    static final int ASIZE = 128;
    static final int ABITS = 7;  // ASIZE = 2^ABITS
    static final int VSIZE = 8;
    private ConstInfo[][] objects;
    private int elements;

    public LongVector() {
        objects = new ConstInfo[VSIZE][];
        elements = 0;
    }

    public LongVector(int initialSize) {
        int vsize = ((initialSize >> ABITS) & ~(VSIZE - 1)) + VSIZE;
        objects = new ConstInfo[vsize][];
        elements = 0;
    }

    public int size() { return elements; }

    public int capacity() { return objects.length * ASIZE; }

    public ConstInfo elementAt(int i) {
        if (i < 0 || elements <= i)
            return null;

        return objects[i >> ABITS][i & (ASIZE - 1)];
    }

    public void addElement(ConstInfo value) {
        int nth = elements >> ABITS;
        int offset = elements & (ASIZE - 1);
        int len = objects.length;
        if (nth >= len) { 
            ConstInfo[][] newObj = new ConstInfo[len + VSIZE][];
            System.arraycopy(objects, 0, newObj, 0, len);
            objects = newObj;
        }

        if (objects[nth] == null)
            objects[nth] = new ConstInfo[ASIZE];

        objects[nth][offset] = value;
        elements++;
    }
}
