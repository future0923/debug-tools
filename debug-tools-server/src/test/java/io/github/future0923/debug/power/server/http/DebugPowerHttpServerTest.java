package io.github.future0923.debug.power.server.http;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author future0923
 */
class DebugPowerHttpServerTest {

    private static DebugPowerHttpServer debugPowerHttpServer;

    @BeforeAll
    public static void before() {
        debugPowerHttpServer = new DebugPowerHttpServer(8888);
    }

    @Test
    public void start() throws InterruptedException {
        debugPowerHttpServer.start();
        Thread.sleep(10000000L);
    }

}