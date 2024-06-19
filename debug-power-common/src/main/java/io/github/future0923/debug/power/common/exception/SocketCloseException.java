package io.github.future0923.debug.power.common.exception;

/**
 * @author future0923
 */
public class SocketCloseException extends DebugPowerException {

    public SocketCloseException() {
    }

    public SocketCloseException(String message) {
        super(message);
    }

    public SocketCloseException(String message, Throwable cause) {
        super(message, cause);
    }

    public SocketCloseException(Throwable cause) {
        super(cause);
    }

    public SocketCloseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
