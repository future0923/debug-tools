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
