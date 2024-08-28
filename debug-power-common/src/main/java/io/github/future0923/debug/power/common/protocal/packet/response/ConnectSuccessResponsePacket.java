package io.github.future0923.debug.power.common.protocal.packet.response;

import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.common.protocal.Command;
import io.github.future0923.debug.power.common.protocal.packet.Packet;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;

/**
 * @author future0923
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ConnectSuccessResponsePacket extends Packet {

    private static final Logger logger = Logger.getLogger(ConnectSuccessResponsePacket.class);

    private String applicationName;

    private Integer httpListenPort;

    @Override
    public Byte getCommand() {
        return Command.CONNECT_SUCCESS_RESPONSE;
    }

    @Override
    public byte[] binarySerialize() {
        return DebugPowerJsonUtils.toJsonStr(this).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void binaryDeserialization(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return;
        }
        String jsonString = new String(bytes, StandardCharsets.UTF_8);
        if (!DebugPowerJsonUtils.isTypeJSON(jsonString)) {
            logger.warning("The data ConnectSuccessResponsePacket received is not JSON, {}", jsonString);
            return;
        }
        ConnectSuccessResponsePacket packet = DebugPowerJsonUtils.toBean(jsonString, ConnectSuccessResponsePacket.class);
        this.setHttpListenPort(packet.getHttpListenPort());
        this.setApplicationName(packet.getApplicationName());
    }

    public static ConnectSuccessResponsePacket of(String applicationName, Integer httpListenPort) {
        ConnectSuccessResponsePacket packet = new ConnectSuccessResponsePacket();
        packet.setApplicationName(applicationName);
        packet.setHttpListenPort(httpListenPort);
        return packet;
    }

}
