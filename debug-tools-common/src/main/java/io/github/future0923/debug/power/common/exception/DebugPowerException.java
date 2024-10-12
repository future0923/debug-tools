package io.github.future0923.debug.power.common.exception;

/**
 * @author future0923
 */
public class DebugPowerException extends Exception{

    public DebugPowerException() {
    }

    public DebugPowerException(String message) {
        super(message);
    }

    public DebugPowerException(String message, Throwable cause) {
        super(message, cause);
    }

    public DebugPowerException(Throwable cause) {
        super(cause);
    }

    public DebugPowerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
