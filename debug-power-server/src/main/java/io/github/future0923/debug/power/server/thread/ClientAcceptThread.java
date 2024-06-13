package io.github.future0923.debug.power.server.thread;

import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.common.handler.PacketHandleService;
import io.github.future0923.debug.power.server.handler.ServerPacketHandleService;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author future0923
 */
public class ClientAcceptThread extends Thread {

    private static final Logger logger = Logger.getLogger(ClientAcceptThread.class);

    private final Map<ClientHandleThread, Long> lastUpdateTime2Thread = new ConcurrentHashMap<>();

    private final PacketHandleService packetHandleService = new ServerPacketHandleService();

    public ClientAcceptThread() {
        setName("DebugPower-ClientAccept-Thread");
        setDaemon(true);
    }

    @Override
    public void run() {
        new SessionCheckThread(lastUpdateTime2Thread).start();
        try {
            ServerSocket serverSocket = getServerSocketByDynamicPort(50888);
            int bindPort = serverSocket.getLocalPort();
            logger.info("start server trans and bind port in {}", bindPort);
            while (true) {
                Socket socket = serverSocket.accept();
                logger.info("get client conn start handle thread socket: {}", socket);
                ClientHandleThread socketHandleThread = new ClientHandleThread(socket, lastUpdateTime2Thread, packetHandleService);
                socketHandleThread.start();
                lastUpdateTime2Thread.put(socketHandleThread, System.currentTimeMillis());
            }
        } catch (Exception e) {
            logger.error("运行过程中发生异常，关闭对应链接:{}", e);
        }
    }

    public ServerSocket getServerSocketByDynamicPort(int port) {
        ServerSocket serverSocket = null;
        int i = 0;
        while (i < 5) {
            try {
                serverSocket = new ServerSocket(port + i);
                return serverSocket;
            } catch (Exception e) {
                int currentPort = port + i;
                logger.error("{}端口绑定,失败:{}", currentPort, e);
                ++i;
            }
        }
        return serverSocket;
    }
}
