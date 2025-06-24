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
package io.github.future0923.debug.tools.hotswap.core.javassist.runtime;

/**
 * A support class for implementing <code>$cflow</code>.
 * This support class is required at runtime
 * only if <code>$cflow</code> is used.
 *
 * @see javassist.CtBehavior#useCflow(String)
 */
public class Cflow extends ThreadLocal<Cflow.Depth> {
    protected static class Depth {
        private int depth;
        Depth() { depth = 0; }
        int value() { return depth; }
        void inc() { ++depth; }
        void dec() { --depth; }
    }

    @Override
    protected synchronized Depth initialValue() {
        return new Depth();
    }

    /**
     * Increments the counter.
     */
    public void enter() { get().inc(); }

    /**
     * Decrements the counter.
     */
    public void exit() { get().dec(); }

    /**
     * Returns the value of the counter.
     */
    public int value() { return get().value(); }
}
