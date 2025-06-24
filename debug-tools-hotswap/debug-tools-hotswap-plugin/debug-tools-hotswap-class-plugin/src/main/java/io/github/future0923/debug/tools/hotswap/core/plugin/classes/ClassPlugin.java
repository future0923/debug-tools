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
