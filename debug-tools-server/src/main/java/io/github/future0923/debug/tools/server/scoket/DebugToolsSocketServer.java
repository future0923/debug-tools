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
package io.github.future0923.debug.tools.server.scoket;

import io.github.future0923.debug.tools.server.thread.ClientAcceptThread;
import io.github.future0923.debug.tools.server.thread.SessionCheckThread;
import io.github.future0923.debug.tools.server.thread.SocketServerHolder;

import java.util.concurrent.CountDownLatch;

/**
 * @author future0923
 */
public class DebugToolsSocketServer {

    public final ClientAcceptThread clientAcceptThread;

    private final SessionCheckThread sessionCheckThread;

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    public DebugToolsSocketServer() {
        clientAcceptThread = new ClientAcceptThread(countDownLatch);
        SocketServerHolder.setClientAcceptThread(clientAcceptThread);
        sessionCheckThread = new SessionCheckThread(clientAcceptThread.getLastUpdateTime2Thread());
        SocketServerHolder.setSessionCheckThread(sessionCheckThread);
    }

    public void start() {
        clientAcceptThread.start();
        sessionCheckThread.start();
        try {
            countDownLatch.await();
        } catch (InterruptedException ignored) {
        }
    }

    public void close() {
        clientAcceptThread.close();
        sessionCheckThread.interrupt();
    }
}
