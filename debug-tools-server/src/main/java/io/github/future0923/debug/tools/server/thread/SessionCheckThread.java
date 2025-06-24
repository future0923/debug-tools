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
package io.github.future0923.debug.tools.server.thread;

import io.github.future0923.debug.tools.base.logging.Logger;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author future0923
 */
public class SessionCheckThread extends Thread {

    private static final Logger logger = Logger.getLogger(SessionCheckThread.class);

    private final Long HEALTH_CHECK_TIMEOUT;

    private final Long INTERVAL;

    private final Map<ClientHandleThread, Long> lastUpdateTime2Thread;

    public SessionCheckThread(Map<ClientHandleThread, Long> lastUpdateTime2Thread) {
        setDaemon(true);
        setName("DebugTools-SessionCheck-Thread");
        this.HEALTH_CHECK_TIMEOUT = TimeUnit.SECONDS.toMillis(180L);
        this.INTERVAL = 30L;
        this.lastUpdateTime2Thread = lastUpdateTime2Thread;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TimeUnit.SECONDS.sleep(this.INTERVAL);
            } catch (InterruptedException e) {
                return;
            }
            try {
                long nowTime = System.currentTimeMillis();
                Iterator<Map.Entry<ClientHandleThread, Long>> iterator = lastUpdateTime2Thread.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<ClientHandleThread, Long> next = iterator.next();
                    ClientHandleThread socketHandleThread = next.getKey();
                    Long time = next.getValue();
                    long expireTime = nowTime - time;
                    if (expireTime > HEALTH_CHECK_TIMEOUT) {
                        socketHandleThread.setClosed(true);
                        iterator.remove();
                        logger.info("thread is un conn , stop and remove thread : {}", socketHandleThread.getSocket());
                    }
                }
            } catch (Exception e) {
                logger.error("SessionCheckThread happen error : {}", e);
            }
        }
    }
}
