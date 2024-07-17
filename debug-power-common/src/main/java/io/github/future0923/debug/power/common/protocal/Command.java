package io.github.future0923.debug.power.common.protocal;

/**
 * @author future0923
 */
public interface Command {

    Byte HEARTBEAT_REQUEST = 1;

    Byte HEARTBEAT_RESPONSE = 2;

    Byte RUN_TARGET_METHOD_REQUEST = 3;

    Byte RUN_TARGET_METHOD_RESPONSE = 4;

    Byte SERVER_CLOSE_REQUEST = 5;
}
