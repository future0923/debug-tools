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
package io.github.future0923.debug.tools.hotswap.core.watch;

import io.github.future0923.debug.tools.base.utils.DebugToolsOSUtils;
import io.github.future0923.debug.tools.hotswap.core.watch.nio.TreeWatcherNIO;
import io.github.future0923.debug.tools.hotswap.core.watch.nio.WatcherNIO2;

import java.io.IOException;

/**
 * 观察者工厂
 */
public class WatcherFactory {

    public static double JAVA_VERSION = getVersion();

    static double getVersion() {
        String version = System.getProperty("java.version");

        int pos = 0;
        boolean decimalPart = false;

        for (; pos < version.length(); pos++) {
            char c = version.charAt(pos);
            if ((c < '0' || c > '9') && c != '.') break;
            if (c == '.') {
                if (decimalPart) break;
                decimalPart = true;
            }
        }
        return Double.parseDouble(version.substring(0, pos));
    }

    public Watcher getWatcher() throws IOException {
        if (JAVA_VERSION >= 1.7) {
            if (DebugToolsOSUtils.isWindows()) {
                return new TreeWatcherNIO();
            } else {
                return new WatcherNIO2();
            }
        } else {
            throw new UnsupportedOperationException("Watcher is implemented only for Java 1.7 (NIO2). " +
                    "JNotify implementation should be added in the future for older Java version support.");
        }

    }
}
