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
package io.github.future0923.debug.tools.server.groovy;

import io.github.future0923.debug.tools.server.utils.DebugToolsEnvUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DebugToolsGroovyScriptTest {

    @Test
    void beanDefinitionHelpersDoNotExposeSpringTypeInPublicSignature() throws Exception {
        assertEquals(Object.class, method(DebugToolsGroovyScript.class, "gtbd").getReturnType());
        assertEquals(Object.class, method(DebugToolsGroovyScript.class, "getBeanDefinition").getReturnType());
        assertEquals(Object.class, method(DebugToolsEnvUtils.class, "getBeanDefinition").getReturnType());
    }

    private static Method method(Class<?> type, String name) throws NoSuchMethodException {
        return type.getMethod(name, String.class);
    }
}
