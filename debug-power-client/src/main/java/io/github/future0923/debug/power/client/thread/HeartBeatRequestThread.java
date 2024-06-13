package io.github.future0923.debug.power.client.thread;

import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.client.holder.ClientSocketHolder;
import io.github.future0923.debug.power.common.protocal.packet.request.HeartBeatRequestPacket;
import io.github.future0923.debug.power.common.utils.DebugPowerIOUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author future0923
 */
public class HeartBeatRequestThread extends Thread {

    private static final Logger logger = Logger.getLogger(HeartBeatRequestThread.class);

    private final ClientSocketHolder holder;

    private final Long interval;

    public HeartBeatRequestThread(ClientSocketHolder holder, Long interval) {
        setDaemon(true);
        setName("DebugPower-HeartBeatRequest-Thread");
        this.holder = holder;
        this.interval = interval;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (!holder.isClosed()) {
                    DebugPowerIOUtils.writeAndFlush(holder.getOutputStream(), new HeartBeatRequestPacket());
                } else {
                    logger.warning("HeartBeatRequest reconnect debug power server");
                    holder.connect();
                }
                TimeUnit.SECONDS.sleep(this.interval);
            } catch (Exception e) {
                logger.error("HeartBeatRequest happen error, Try again in {} seconds.", e, this.interval);
            }
        }
    }
}