package io.github.future0923.debug.power.common.exception;

/**
 * @author future0923
 */
public class DebugPowerRuntimeException extends RuntimeException{

    public DebugPowerRuntimeException() {
    }

    public DebugPowerRuntimeException(String message) {
        super(message);
    }

    public DebugPowerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DebugPowerRuntimeException(Throwable cause) {
        super(cause);
    }

    public DebugPowerRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
