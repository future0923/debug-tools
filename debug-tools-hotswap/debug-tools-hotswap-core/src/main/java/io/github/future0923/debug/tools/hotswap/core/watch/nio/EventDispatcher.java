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
package io.github.future0923.debug.tools.hotswap.core.watch.nio;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchEventListener;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchFileEvent;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Event分发器
 */
public class EventDispatcher implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(EventDispatcher.class);

    /**
     * PATH变动的监听者
     */
    private final Map<Path, List<WatchEventListener>> listeners;

    /**
     * 工作队列。事件队列{@link #eventQueue}调用drainTo后，所有待处理的事件都添加到此列表中
     */
    private final ArrayList<Event> working = new ArrayList<>();

    /**
     * 待分发的事件
     */
    private final ArrayBlockingQueue<Event> eventQueue = new ArrayBlockingQueue<>(500);

    /**
     * 分发线程
     */
    private Thread runnable = null;

    public EventDispatcher(Map<Path, List<WatchEventListener>> listeners) {
        super();
        this.listeners = listeners;
    }

    @Override
    public void run() {
        while (true) {
            // 如果工作队列中有未处理的先处理
            for (Event e : working) {
                callListeners(e.event, e.path);
                if (Thread.interrupted()) {
                    return;
                }
                Thread.yield();
            }
            // 取出事件方法working
            eventQueue.drainTo(working);

            // 处理新drainTo到工作队列中的事件
            for (Event e : working) {
                callListeners(e.event, e.path);
                if (Thread.interrupted()) {
                    return;
                }
                Thread.yield();
            }

            // 处理完清空工作队列
            working.clear();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                return;
            }
        }
    }

    /**
     * 添加待分发的事件到事件队列
     */
    public void add(WatchEvent<Path> event, Path path) {
        eventQueue.offer(new Event(event, path));
    }

    /**
     * 调用监听者
     */
    private void callListeners(final WatchEvent<?> event, final Path path) {
        boolean matchedOne = false;
        for (Map.Entry<Path, List<WatchEventListener>> list : listeners.entrySet()) {
            if (path.startsWith(list.getKey())) {
                matchedOne = true;
                for (WatchEventListener listener : new ArrayList<>(list.getValue())) {
                    WatchFileEvent agentEvent = new HotswapWatchFileEvent(event, path);
                    try {
                        listener.onEvent(agentEvent);
                    } catch (Throwable e) {
                         LOGGER.error("Error in watch event '{}' listener'{}'", e, agentEvent, listener);
                    }
                }
            }
        }
        if (!matchedOne) {
            LOGGER.error("No match for  watch event '{}',  path '{}'", event, path);
        }
    }

    /**
     * 启动分发线程
     */
    public void start() {
        runnable = new Thread(this);
        runnable.setDaemon(true);
        runnable.setName("HotSwap Dispatcher");
        runnable.start();
    }

    /**
     * 停止分发线程
     */
    public void stop() throws InterruptedException {
        if (runnable != null) {
            runnable.interrupt();
            runnable.join();
        }
        runnable = null;
    }

    /**
     * 要分发的事件对象
     */
    static class Event {

        /**
         * 事件
         */
        final WatchEvent<Path> event;

        /**
         * 路径
         */
        final Path path;

        public Event(WatchEvent<Path> event, Path path) {
            super();
            this.event = event;
            this.path = path;
        }
    }
}
