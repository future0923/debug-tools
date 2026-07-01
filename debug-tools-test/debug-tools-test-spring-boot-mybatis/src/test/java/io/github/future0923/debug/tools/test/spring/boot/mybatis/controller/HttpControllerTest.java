/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.test.spring.boot.mybatis.controller;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpControllerTest {

    private final TestRequestMappingHandlerMapping handlerMapping = new TestRequestMappingHandlerMapping();

    @Test
    void registersManySpringHttpRequestForms() throws NoSuchMethodException {
        Map<Set<String>, Set<RequestMethod>> mappings = findMappings();

        assertThat(mappings)
                .containsEntry(set("/http/get"), set(RequestMethod.GET))
                .containsEntry(set("/http/get/alias", "/http/get/alias2"), set(RequestMethod.GET))
                .containsEntry(set("/http/get/{id}"), set(RequestMethod.GET))
                .containsEntry(set("/http/post/json"), set(RequestMethod.POST))
                .containsEntry(set("/http/post/form"), set(RequestMethod.POST))
                .containsEntry(set("/http/post/form-data"), set(RequestMethod.POST))
                .containsEntry(set("/http/post/binary/bytes"), set(RequestMethod.POST))
                .containsEntry(set("/http/post/binary/stream"), set(RequestMethod.POST))
                .containsEntry(set("/http/put/{id}"), set(RequestMethod.PUT))
                .containsEntry(set("/http/delete/{id}"), set(RequestMethod.DELETE))
                .containsEntry(set("/http/patch/{id}"), set(RequestMethod.PATCH))
                .containsEntry(set("/http/head"), set(RequestMethod.HEAD))
                .containsEntry(set("/http/options"), set(RequestMethod.OPTIONS))
                .containsEntry(set("/http/request/multi-method"), set(RequestMethod.GET, RequestMethod.POST))
                .containsEntry(set("/http/request/params"), set(RequestMethod.GET))
                .containsEntry(set("/http/request/headers"), set(RequestMethod.GET))
                .containsEntry(set("/http/request/consumes"), set(RequestMethod.POST))
                .containsEntry(set("/http/request/produces"), set(RequestMethod.GET))
                .containsEntry(set("/http/regex/{code:[0-9]+}"), set(RequestMethod.GET))
                .containsEntry(set("/http/matrix/{category}"), set(RequestMethod.GET))
                .containsEntry(set("/http/nested/{tenant}/orders/{orderId}"), set(RequestMethod.GET));
    }

    @Test
    void getPathVariableReturnsResolvedPath() {
        HttpController controller = new HttpController();

        Map<String, Object> result = controller.getPathVariable(23L);

        assertThat(result)
                .containsEntry("method", "GET")
                .containsEntry("path", "/http/get/23")
                .containsEntry("id", 23L);
    }

    @Test
    void postBinaryBytesReturnsBodySize() {
        HttpController controller = new HttpController();

        Map<String, Object> result = controller.postBinaryBytes(new byte[]{0, 1, 2, 3});

        assertThat(result)
                .containsEntry("method", "POST")
                .containsEntry("path", "/http/post/binary/bytes")
                .containsEntry("size", 4);
    }

    @Test
    void postFormDataReturnsTextFieldsAndFileInfo() {
        HttpController controller = new HttpController();
        MockMultipartFile file = new MockMultipartFile("file", "debug-tools.txt", "text/plain", new byte[]{1, 2, 3});

        Map<String, Object> result = controller.postFormData("future", 18, file);

        assertThat(result)
                .containsEntry("method", "POST")
                .containsEntry("path", "/http/post/form-data")
                .containsEntry("username", "future")
                .containsEntry("age", 18)
                .containsEntry("fileName", "debug-tools.txt")
                .containsEntry("fileSize", 3L);
    }

    @Test
    void postBinaryStreamReturnsBodySize() throws Exception {
        HttpController controller = new HttpController();
        javax.servlet.ServletInputStream inputStream = new javax.servlet.ServletInputStream() {
            private final ByteArrayInputStream delegate = new ByteArrayInputStream(new byte[]{0, 1, 2});

            @Override
            public boolean isFinished() {
                return delegate.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(javax.servlet.ReadListener readListener) {
            }

            @Override
            public int read() {
                return delegate.read();
            }
        };
        javax.servlet.http.HttpServletRequest request = mock(javax.servlet.http.HttpServletRequest.class);
        when(request.getInputStream()).thenReturn(inputStream);

        Map<String, Object> result = controller.postBinaryStream(request);

        assertThat(result)
                .containsEntry("method", "POST")
                .containsEntry("path", "/http/post/binary/stream")
                .containsEntry("size", 3);
    }

    private Map<Set<String>, Set<RequestMethod>> findMappings() throws NoSuchMethodException {
        return Arrays.stream(HttpController.class.getDeclaredMethods())
                .map(method -> handlerMapping.getMappingForMethod(method, HttpController.class))
                .filter(info -> info != null)
                .collect(Collectors.toMap(
                info -> info.getPatternsCondition().getPatterns(),
                info -> info.getMethodsCondition().getMethods()
        ));
    }

    private static Set<String> set(String... values) {
        return new HashSet<>(Arrays.asList(values));
    }

    private static Set<RequestMethod> set(RequestMethod... values) {
        return new HashSet<>(Arrays.asList(values));
    }

    private static class TestRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

        @Override
        public RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
            return super.getMappingForMethod(method, handlerType);
        }
    }
}
