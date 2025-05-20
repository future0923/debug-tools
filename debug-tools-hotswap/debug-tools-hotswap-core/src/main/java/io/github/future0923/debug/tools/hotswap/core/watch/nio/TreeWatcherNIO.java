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
package io.github.future0923.debug.tools.hotswap.core.watch.nio;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

/**
 * NIO watcher实现.
 */
public class TreeWatcherNIO extends AbstractNIO2Watcher {

    private final static WatchEvent.Modifier HIGH;
    private final static WatchEvent.Modifier FILE_TREE;
    private final static WatchEvent.Modifier[] MODIFIERS;

    static {
        HIGH =  getWatchEventModifier("com.sun.nio.file.SensitivityWatchEventModifier","HIGH");
        FILE_TREE = getWatchEventModifier("com.sun.nio.file.ExtendedWatchEventModifier", "FILE_TREE");

        if(FILE_TREE != null) {
            MODIFIERS =  new WatchEvent.Modifier[] { FILE_TREE, HIGH };
        } else {
            MODIFIERS =  new WatchEvent.Modifier[] { HIGH };
        }
    }

    public TreeWatcherNIO() throws IOException {
        super();
    }

    /**
     * 向WatchService中注册路径
     */
    private void register(Path dir) throws IOException {

        for(Path p: keys.values()) {
            if(dir.startsWith(p)) {
                LOGGER.debug("Path {} watched via {}", dir, p);
                return;
            }
        }

        if (FILE_TREE == null) {
            LOGGER.debug("WATCHING:ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY - high} {}", dir);
        } else {
            LOGGER.debug("WATCHING: ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY - fileTree,high {}", dir);
        }

        final WatchKey key = dir.register(watcher, KINDS,  MODIFIERS);

        keys.put(key, dir);
    }

    @Override
    protected void registerAll(Path dir, boolean fromCreateEvent) throws IOException {
        LOGGER.debug("Registering directory {} ", dir);
        register(dir);
    }
}