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
package io.github.future0923.debug.tools.server.http.handler;

import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AllClassLoaderHttpHandlerTest {

    @Test
    void defaultIdentityPrefersUrlClassLoaderOverAppClassLoader() {
        Set<AllClassLoaderRes.Item> items = new LinkedHashSet<>();
        items.add(new AllClassLoaderRes.Item("jdk.internal.loader.ClassLoaders$AppClassLoader", "app"));
        items.add(new AllClassLoaderRes.Item("java.net.URLClassLoader", "url"));

        assertEquals("url", AllClassLoaderHttpHandler.getDefaultIdentity(items));
    }

    @Test
    void defaultIdentityKeepsSpringBootClassLoaderAheadOfUrlClassLoader() {
        Set<AllClassLoaderRes.Item> items = new LinkedHashSet<>();
        items.add(new AllClassLoaderRes.Item("java.net.URLClassLoader", "url"));
        items.add(new AllClassLoaderRes.Item("org.springframework.boot.devtools.restart.classloader.RestartClassLoader", "restart"));

        assertEquals("restart", AllClassLoaderHttpHandler.getDefaultIdentity(items));
    }

    @Test
    void defaultIdentityFallsBackToAppClassLoader() {
        Set<AllClassLoaderRes.Item> items = new LinkedHashSet<>();
        items.add(new AllClassLoaderRes.Item("jdk.internal.loader.ClassLoaders$AppClassLoader", "app"));

        assertEquals("app", AllClassLoaderHttpHandler.getDefaultIdentity(items));
    }
}
