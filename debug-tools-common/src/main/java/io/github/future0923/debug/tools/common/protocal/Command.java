package io.github.future0923.debug.tools.common.protocal;

/**
 * @author future0923
 */
public interface Command {

    Byte HEARTBEAT_REQUEST = 1;

    Byte HEARTBEAT_RESPONSE = 2;

    Byte RUN_TARGET_METHOD_REQUEST = 3;

    Byte RUN_TARGET_METHOD_RESPONSE = 4;

    Byte SERVER_CLOSE_REQUEST = 5;

    Byte CLEAR_RUN_RESULT = 7;

    Byte RUN_GROOVY_SCRIPT_REQUEST = 8;

    Byte RUN_GROOVY_SCRIPT_RESPONSE = 9;

    Byte HOTSWAP_REQUEST = 10;

    Byte HOTSWAP_RESPONSE = 11;

    Byte DYNAMIC_COMPILER_REQUEST = 12;
}
