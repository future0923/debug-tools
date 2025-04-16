/*
 * Copyright 2013-2024 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
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