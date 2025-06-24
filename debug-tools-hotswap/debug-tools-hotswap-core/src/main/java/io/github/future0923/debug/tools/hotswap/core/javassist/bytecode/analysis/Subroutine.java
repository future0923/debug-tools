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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a nested method subroutine (marked by JSR and RET).
 *
 * @author Jason T. Greene
 */
public class Subroutine {
    //private Set callers = new HashSet();
    private List<Integer> callers = new ArrayList<Integer>();
    private Set<Integer> access = new HashSet<Integer>();
    private int start;

    public Subroutine(int start, int caller) {
        this.start = start;
        callers.add(caller);
    }

    public void addCaller(int caller) {
        callers.add(caller);
    }

    public int start() {
        return start;
    }

    public void access(int index) {
        access.add(index);
    }

    public boolean isAccessed(int index) {
        return access.contains(index);
    }

    public Collection<Integer> accessed() {
        return access;
    }

    public Collection<Integer> callers() {
        return callers;
    }

    @Override
    public String toString() {
        return "start = " + start + " callers = " + callers.toString();
    }
}
