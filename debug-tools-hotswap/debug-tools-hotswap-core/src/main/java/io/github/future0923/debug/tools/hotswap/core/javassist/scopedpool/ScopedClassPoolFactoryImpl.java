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
package io.github.future0923.debug.tools.hotswap.core.javassist.scopedpool;

import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;

/**
 * An implementation of factory.
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.5 $
 */
public class ScopedClassPoolFactoryImpl implements ScopedClassPoolFactory {
    /**
     * Makes an instance.
     */
    public ScopedClassPool create(ClassLoader cl, ClassPool src,
                                  ScopedClassPoolRepository repository) {
        return new ScopedClassPool(cl, src, repository, false);
    }

    /**
     * Makes an instance.
     */
    public ScopedClassPool create(ClassPool src,
                                  ScopedClassPoolRepository repository) {
        return new ScopedClassPool(null, src, repository, true);
    }
}
