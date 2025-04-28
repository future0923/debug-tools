package io.github.future0923.debug.tools.extension.spring;

import io.github.future0923.debug.tools.extension.spring.reactive.MockServerHttpRequest;
import io.github.future0923.debug.tools.extension.spring.reactive.MockServerHttpResponse;
import io.github.future0923.debug.tools.extension.spring.reactive.MockServerWebExchange;

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
