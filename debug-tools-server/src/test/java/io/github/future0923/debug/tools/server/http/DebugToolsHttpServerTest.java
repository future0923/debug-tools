package io.github.future0923.debug.tools.server.http;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author future0923
 */
class DebugToolsHttpServerTest {

    private static DebugToolsHttpServer debugToolsHttpServer;

    @BeforeAll
    public static void before() {
        debugToolsHttpServer = new DebugToolsHttpServer(8888);
    }

    @Test
    public void start() throws InterruptedException {
        debugToolsHttpServer.start();
        Thread.sleep(10000000L);
    }

}