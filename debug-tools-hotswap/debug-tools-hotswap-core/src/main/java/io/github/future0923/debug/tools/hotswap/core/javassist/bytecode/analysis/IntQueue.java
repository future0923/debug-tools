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

import java.util.NoSuchElementException;

class IntQueue {
    private static class Entry {
        private Entry next;
        private int value;
        private Entry(int value) {
            this.value = value;
        }
    }
    private Entry head;

    private Entry tail;

    void add(int value) {
        Entry entry = new Entry(value);
        if (tail != null)
            tail.next = entry;
        tail = entry;

        if (head == null)
            head = entry;
    }

    boolean isEmpty() {
        return head == null;
    }

    int take() {
        if (head == null)
            throw new NoSuchElementException();

        int value = head.value;
        head = head.next;
        if (head == null)
            tail = null;

        return value;
    }
}
