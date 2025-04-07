package io.github.future0923.debug.tools.server.mock.spring;

import io.github.future0923.debug.tools.server.mock.spring.reactive.MockServerHttpRequest;
import io.github.future0923.debug.tools.server.mock.spring.reactive.MockServerHttpResponse;
import io.github.future0923.debug.tools.server.mock.spring.reactive.MockServerWebExchange;

/**
 * @author future0923
 */
public class SpringReactiveUtil {

    public static MockServerHttpRequest getServerHttpRequest() {
        return MockServerHttpRequest.get("/debug-tools-mock").build();
    }

    public static MockServerWebExchange getServerWebExchange() {
        return MockServerWebExchange.from(getServerHttpRequest());
    }

    public static MockServerHttpResponse getServerHttpResponse() {
        return new MockServerHttpResponse();
    }
}
