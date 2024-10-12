package io.github.future0923.debug.power.client.thread;

import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.client.holder.ClientSocketHolder;
import io.github.future0923.debug.power.common.handler.PacketHandleService;
import io.github.future0923.debug.power.common.protocal.packet.Packet;
import io.github.future0923.debug.power.common.protocal.packet.PacketCodec;

/**
 * @author future0923
 */
public class ServerHandleThread extends Thread {

    private static final Logger logger = Logger.getLogger(ServerHandleThread.class);

    private final ClientSocketHolder holder;

    private final PacketHandleService packetHandleService;

    public ServerHandleThread(ClientSocketHolder holder, PacketHandleService packetHandleService) {
        setDaemon(true);
        setName("DebugPower-ServerHandle-Thread-" + holder.getConfig().getPort());
        this.holder = holder;
        this.packetHandleService = packetHandleService;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (holder.isClosed()) {
                logger.warning("debug power client disconnect the link, waiting to reconnect");
                break;
            }
            try {
                Packet packet = PacketCodec.INSTANCE.getPacket(holder.getInputStream(), holder.getSocket());
                if (packet != null) {
                    packetHandleService.handle(holder.getOutputStream(), packet);
                }
            } catch (Exception e) {
                logger.error("socket io error :{} , error:{}", holder.getSocket(), e);
                break;
            }
        }
    }
}