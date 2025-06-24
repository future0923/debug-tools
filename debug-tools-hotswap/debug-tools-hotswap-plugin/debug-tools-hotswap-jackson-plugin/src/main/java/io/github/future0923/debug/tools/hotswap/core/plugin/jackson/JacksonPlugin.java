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
package io.github.future0923.debug.tools.hotswap.core.plugin.jackson;

import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.plugin.jackson.command.JacksonReloadCommand;

/**
 * @author future0923
 */
@Plugin(
        name = "Jackson",
        description = "Reload Jackson cache after class definition/change.",
        testedVersions = {"All between 2.13.4"}
)
public class JacksonPlugin {

    @Init
    static Scheduler scheduler;

    private static boolean isJacksonEnv = false;

    @OnClassLoadEvent(classNameRegexp = "com.fasterxml.jackson.databind.ObjectMapper")
    public static void register(ClassLoader classLoader) throws NotFoundException, CannotCompileException {
        isJacksonEnv = true;
    }

    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE)
    public static void redefineClass(final Class<?> clazz, final ClassLoader appClassLoader) {
        if (isJacksonEnv) {
            scheduler.scheduleCommand(new JacksonReloadCommand(clazz, appClassLoader), 1000);
        }
    }
}
