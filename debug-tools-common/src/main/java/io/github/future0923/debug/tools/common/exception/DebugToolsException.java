package io.github.future0923.debug.tools.common.exception;

/**
 * @author future0923
 */
public class DebugToolsException extends Exception{

    public DebugToolsException() {
    }

    public DebugToolsException(String message) {
        super(message);
    }

    public DebugToolsException(String message, Throwable cause) {
        super(message, cause);
    }

    public DebugToolsException(Throwable cause) {
        super(cause);
    }

    public DebugToolsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
