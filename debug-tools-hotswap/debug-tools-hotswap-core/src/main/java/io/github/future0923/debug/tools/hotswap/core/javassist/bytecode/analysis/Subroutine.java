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
