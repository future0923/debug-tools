package io.github.future0923.debug.power.server.http.headler;

import com.sun.net.httpserver.Headers;
import io.github.future0923.debug.power.base.constants.ProjectConstants;

/**
 * @author future0923
 */
public class IndexHttpHandler extends BaseHttpHandler<String, String> {

    public static final IndexHttpHandler INSTANCE = new IndexHttpHandler();

    private IndexHttpHandler() {

    }

    @Override
    protected String doHandle(String req, Headers responseHeaders) {
        return "Hello " + ProjectConstants.NAME + " " + ProjectConstants.VERSION;
    }
}
