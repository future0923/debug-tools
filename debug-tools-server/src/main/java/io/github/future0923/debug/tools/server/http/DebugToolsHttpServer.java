package io.github.future0923.debug.tools.server.http;

import com.sun.net.httpserver.HttpServer;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.server.http.handler.AllClassLoaderHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.GetApplicationNameHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.IndexHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.RunResultDetailHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.RunResultTypeHttpHandler;
import lombok.Getter;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author future0923
 */
public class DebugToolsHttpServer {

    private static final Logger logger = Logger.getLogger(DebugToolsHttpServer.class);

    private static volatile DebugToolsHttpServer debugToolsHttpServer;

    private HttpServer httpServer;

    @Getter
    private final int port;

    private volatile boolean started = false;

    public DebugToolsHttpServer(int port) {
        this.port = port;
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            logger.error("start http server bind port in {} error", e, port);
            return;
        }
        httpServer.createContext(IndexHttpHandler.PATH, IndexHttpHandler.INSTANCE);
        httpServer.createContext(RunResultTypeHttpHandler.PATH, RunResultTypeHttpHandler.INSTANCE);
        httpServer.createContext(RunResultDetailHttpHandler.PATH, RunResultDetailHttpHandler.INSTANCE);
        httpServer.createContext(AllClassLoaderHttpHandler.PATH, AllClassLoaderHttpHandler.INSTANCE);
        httpServer.createContext(GetApplicationNameHttpHandler.PATH, GetApplicationNameHttpHandler.INSTANCE);
    }

    public void start() {
        if (!started && httpServer != null) {
            httpServer.start();
            started = true;
            logger.info("start http server trans and bind port in {}", port);
        }
    }

    public void close() {
        if (httpServer != null) {
            httpServer.stop(0);
            started = false;
        }
    }

}
