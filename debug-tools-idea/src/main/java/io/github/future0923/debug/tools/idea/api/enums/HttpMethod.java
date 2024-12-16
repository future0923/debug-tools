package io.github.future0923.debug.tools.idea.api.enums;

import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author future0923
 */
public enum HttpMethod {

    /**
     * ApiInfo
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
