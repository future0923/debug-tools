package io.github.future0923.debug.power.server.http;

import com.sun.net.httpserver.HttpServer;
import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.base.utils.DebugPowerIOUtils;
import io.github.future0923.debug.power.server.http.headler.IndexHttpHandler;
import io.github.future0923.debug.power.server.http.headler.RunResultDetailHttpHandler;
import io.github.future0923.debug.power.server.http.headler.RunResultTypeHttpHandler;
import lombok.Getter;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author future0923
 */
public class DebugPowerHttpServer {

    private static final Logger logger = Logger.getLogger(DebugPowerHttpServer.class);

    private static volatile DebugPowerHttpServer debugPowerHttpServer;

    private HttpServer httpServer;

    @Getter
    private final int listenPort;

    private volatile boolean started = false;

    public static synchronized DebugPowerHttpServer getInstance() {
        if (debugPowerHttpServer == null) {
            debugPowerHttpServer = new DebugPowerHttpServer();
        }
        return debugPowerHttpServer;
    }

    private DebugPowerHttpServer() {
        int availablePort = DebugPowerIOUtils.getAvailablePort(22222);
        this.listenPort = availablePort;
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(availablePort), 0);
        } catch (IOException e) {
            logger.error("start http server bind port in {} error", e, listenPort);
            return;
        }
        httpServer.createContext("/", IndexHttpHandler.INSTANCE);
        httpServer.createContext("/result/type", RunResultTypeHttpHandler.INSTANCE);
        httpServer.createContext("/result/detail", RunResultDetailHttpHandler.INSTANCE);
    }

    public void start() {
        if (!started && httpServer != null) {
            httpServer.start();
            started = true;
            logger.info("start http server trans and bind port in {}", listenPort);
        }
    }

    public void close() {
        if (httpServer != null) {
            httpServer.stop(0);
            started = false;
        }
    }

}
