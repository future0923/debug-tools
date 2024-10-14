package io.github.future0923.debug.tools.common.exception;

/**
 * @author future0923
 */
public class DebugToolsRuntimeException extends RuntimeException{

    public DebugToolsRuntimeException() {
    }

    public DebugToolsRuntimeException(String message) {
        super(message);
    }

    public DebugToolsRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DebugToolsRuntimeException(Throwable cause) {
        super(cause);
    }

    public DebugToolsRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
