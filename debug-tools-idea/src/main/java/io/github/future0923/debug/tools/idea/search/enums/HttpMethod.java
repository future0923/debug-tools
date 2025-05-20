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
package io.github.future0923.debug.tools.idea.search.enums;

import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author future0923
 */
public enum HttpMethod {

    /**
     * REQUEST
     */
    REQUEST,

    /**
     * GET
     */
    GET,

    /**
     * OPTIONS
     */
    OPTIONS,

    /**
     * POST
     */
    POST,

    /**
     * PUT
     */
    PUT,

    /**
     * DELETE
     */
    DELETE,

    /**
     * PATCH
     */
    PATCH,

    /**
     * HEAD
     */
    HEAD,

    /**
     * TRACE
     */
    TRACE,
    ;

    @NotNull
    public static HttpMethod parse(@Nullable Object method) {
        try {
            assert method != null;
            if (method instanceof HttpMethod) {
                return (HttpMethod) method;
            }
            return HttpMethod.valueOf(method.toString());
        } catch (Exception ignore) {
            return REQUEST;
        }
    }

    public static Icon getIcon(HttpMethod httpMethod) {
        if (httpMethod == null) {
            return DebugToolsIcons.HttpMethod.Request;
        }
        return switch (httpMethod) {
            case REQUEST -> DebugToolsIcons.HttpMethod.Request;
            case GET -> DebugToolsIcons.HttpMethod.Get;
            case OPTIONS -> DebugToolsIcons.HttpMethod.Options;
            case POST -> DebugToolsIcons.HttpMethod.Post;
            case PUT -> DebugToolsIcons.HttpMethod.Put;
            case DELETE -> DebugToolsIcons.HttpMethod.Delete;
            case PATCH -> DebugToolsIcons.HttpMethod.Patch;
            case HEAD -> DebugToolsIcons.HttpMethod.Head;
            case TRACE -> DebugToolsIcons.HttpMethod.Trace;
        };
    }
}
