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
package io.github.future0923.debug.tools.hotswap.core.plugin.jackson.command;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.command.MergeableCommand;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import static io.github.future0923.debug.tools.hotswap.core.plugin.jackson.JacksonPlugin.CLEAR_CACHE_METHOD;

/**
 * @author future0923
 */
public class JacksonReloadCommand extends MergeableCommand {

    private static final Logger logger = Logger.getLogger(JacksonReloadCommand.class);

    private final Set<Object> jacksonObj;

    public JacksonReloadCommand(Set<Object> jacksonObj) {
        this.jacksonObj = jacksonObj;
    }

    @Override
    public void executeCommand() {
        Set<Object> copy = Collections.newSetFromMap(new WeakHashMap<>());
        synchronized (jacksonObj) {
            copy.addAll(jacksonObj);
        }
        for (Object obj : copy) {
            try {
                ReflectionHelper.invoke(obj, CLEAR_CACHE_METHOD);
            } catch (Exception e) {
                logger.error("Failed to clear Jackson cache", e);
            }
        }
    }
}
