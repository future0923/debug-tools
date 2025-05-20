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
package io.github.future0923.debug.tools.hotswap.core.plugin.classes;

import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.classes.transformer.AnonymousClassTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.classes.transformer.ClassInitTransformer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author future0923
 */
@Plugin(
        name = "Class",
        testedVersions = {"DCEVM"},
        supportClass = {
                AnonymousClassTransformer.class,
                ClassInitTransformer.class
        }
)
public class ClassPlugin {

    private static final Logger logger = Logger.getLogger(ClassPlugin.class);

    private final Map<Class<?>, byte[]> reloadMap = new HashMap<>();

    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE, skipSynthetic = false)
    public static void debug(final Class<?> clazz) {
        if (ProjectConstants.DEBUG) {
            logger.reload("redefine class {}", clazz.getName());
        }
    }

}
