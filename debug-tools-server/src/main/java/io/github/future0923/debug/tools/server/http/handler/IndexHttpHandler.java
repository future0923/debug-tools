package io.github.future0923.debug.tools.server.http.handler;

import com.sun.net.httpserver.Headers;
import io.github.future0923.debug.tools.base.constants.ProjectConstants;

/**
 * @author future0923
 */
public class IndexHttpHandler extends BaseHttpHandler<String, String> {

    public static final IndexHttpHandler INSTANCE = new IndexHttpHandler();

    public static final String PATH = "/";

    private IndexHttpHandler() {

    }

    @Override
    protected String doHandle(String req, Headers responseHeaders) {
        return "Hello " + ProjectConstants.NAME + " " + ProjectConstants.VERSION;
    }
}
