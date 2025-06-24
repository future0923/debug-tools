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
