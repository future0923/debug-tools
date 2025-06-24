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
package io.github.future0923.debug.tools.hotswap.core.javassist.scopedpool;

import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;

/**
 * A factory interface.
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.4 $
 */
public interface ScopedClassPoolFactory {
    /**
     * Makes an instance.
     */
    ScopedClassPool create(ClassLoader cl, ClassPool src,
                           ScopedClassPoolRepository repository);

    /**
     * Makes an instance.
     */
    ScopedClassPool create(ClassPool src,
                           ScopedClassPoolRepository repository);
}
