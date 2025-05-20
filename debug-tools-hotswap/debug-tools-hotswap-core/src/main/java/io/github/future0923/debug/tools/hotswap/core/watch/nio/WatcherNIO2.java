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

import io.github.future0923.debug.tools.hotswap.core.watch.HotswapAgentWatchEvent;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/**
 * NIO2 watcher实现.
 * <a href="http://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java">例子</a>
 */
public class WatcherNIO2 extends AbstractNIO2Watcher {

    private final static WatchEvent.Modifier HIGH;

    static {
        HIGH = getWatchEventModifier("com.sun.nio.file.SensitivityWatchEventModifier", "HIGH");
    }

    public WatcherNIO2() throws IOException {
        super();
    }

    @Override
    protected void registerAll(final Path dir, boolean fromCreateEvent) throws IOException {
        LOGGER.debug("Registering directory  {}", dir);
        Files.walkFileTree(dir, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (fromCreateEvent) {
                    sendFakeCreateEvents(dir);
                }
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void sendFakeCreateEvents(Path dir) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    String name = entry.getFileName().toString();
                    HotswapAgentWatchEvent<Path> ev = new HotswapAgentWatchEvent(ENTRY_CREATE, name);
                    LOGGER.debug("Fake watch event '{}' on '{}' --> {}", ev.kind().name(), entry, name);
                    dispatcher.add(ev, entry);
                }
            }
        } catch (IOException e) {
            LOGGER.warning("Unable to send fake create events for directory {}", e, dir);
        }
    }

    /**
     * 向WatchService注册给定目录
     */
    private void register(Path dir) throws IOException {
        final WatchKey key = HIGH == null ? dir.register(watcher, KINDS) : dir.register(watcher, KINDS, HIGH);
        keys.put(key, dir);
    }
}