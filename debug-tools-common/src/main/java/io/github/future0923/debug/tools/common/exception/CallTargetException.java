package io.github.future0923.debug.tools.common.exception;

/**
 * @author future0923
 */
public class CallTargetException extends DebugToolsRuntimeException {

    public CallTargetException(String message) {
        super(message);
    }

    public CallTargetException(Throwable cause) {
        super(cause);
    }
}
