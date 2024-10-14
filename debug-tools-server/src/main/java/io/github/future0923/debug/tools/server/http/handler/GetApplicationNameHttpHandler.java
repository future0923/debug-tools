package io.github.future0923.debug.tools.server.http.handler;

import com.sun.net.httpserver.Headers;
import io.github.future0923.debug.tools.common.utils.DebugToolsJvmUtils;

/**
 * @author future0923
 */
public class GetApplicationNameHttpHandler extends BaseHttpHandler<Void, String> {

    public static final GetApplicationNameHttpHandler INSTANCE = new GetApplicationNameHttpHandler();

    public static final String PATH = "/getApplicationName";

    private GetApplicationNameHttpHandler() {

    }

    @Override
    protected String doHandle(Void req, Headers responseHeaders) {
        return DebugToolsJvmUtils.getApplicationName();
    }
}
