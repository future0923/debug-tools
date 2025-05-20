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
