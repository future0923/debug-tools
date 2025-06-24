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
