package io.github.future0923.debug.power.common.exception;

/**
 * @author future0923
 */
public class CallTargetException extends DebugPowerRuntimeException {

    public CallTargetException(String message) {
        super(message);
    }

    public CallTargetException(Throwable cause) {
        super(cause);
    }
}
