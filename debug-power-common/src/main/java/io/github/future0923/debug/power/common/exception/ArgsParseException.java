package io.github.future0923.debug.power.common.exception;

/**
 * @author future0923
 */
public class ArgsParseException extends DebugPowerRuntimeException {

    public ArgsParseException(String message) {
        super(message);
    }

    public ArgsParseException(Throwable cause) {
        super(cause);
    }

    public static void throwEx(String message) throws ArgsParseException {
        throw new ArgsParseException(message);
    }
}
