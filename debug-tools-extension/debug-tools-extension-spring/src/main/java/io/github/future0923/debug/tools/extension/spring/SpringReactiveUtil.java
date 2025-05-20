/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
