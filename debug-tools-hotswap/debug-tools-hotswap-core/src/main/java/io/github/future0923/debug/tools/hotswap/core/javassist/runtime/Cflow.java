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
