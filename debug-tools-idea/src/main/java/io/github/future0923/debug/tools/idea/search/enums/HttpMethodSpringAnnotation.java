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

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Spring方法注解
 */
@Getter
public enum HttpMethodSpringAnnotation {

    /**
     * RequestMapping
     */
    REQUEST_MAPPING("org.springframework.web.bind.annotation.RequestMapping", HttpMethod.REQUEST),

    /**
     * GetMapping
     */
    GET_MAPPING("org.springframework.web.bind.annotation.GetMapping", HttpMethod.GET),

    /**
     * PostMapping
     */
    POST_MAPPING("org.springframework.web.bind.annotation.PostMapping", HttpMethod.POST),

    /**
     * PutMapping
     */
    PUT_MAPPING("org.springframework.web.bind.annotation.PutMapping", HttpMethod.PUT),

    /**
     * DeleteMapping
     */
    DELETE_MAPPING("org.springframework.web.bind.annotation.DeleteMapping", HttpMethod.DELETE),

    /**
     * PatchMapping
     */
    PATCH_MAPPING("org.springframework.web.bind.annotation.PatchMapping", HttpMethod.PATCH),

    /**
     * RequestParam
     */
    REQUEST_PARAM("org.springframework.web.bind.annotation.RequestParam", null),

    /**
     * RequestBody
     */
    REQUEST_BODY("org.springframework.web.bind.annotation.RequestBody", null),

    /**
     * PathVariable
     */
    PATH_VARIABLE("org.springframework.web.bind.annotation.PathVariable", null),

    /**
     * RequestHeader
     */
    REQUEST_HEADER("org.springframework.web.bind.annotation.RequestHeader", null);

    private final String qualifiedName;
    private final HttpMethod method;

    HttpMethodSpringAnnotation(String qualifiedName, HttpMethod method) {
        this.qualifiedName = qualifiedName;
        this.method = method;
    }

    @Nullable
    public static HttpMethodSpringAnnotation getByQualifiedName(String qualifiedName) {
        for (HttpMethodSpringAnnotation springRequestAnnotation : HttpMethodSpringAnnotation.values()) {
            if (springRequestAnnotation.getQualifiedName().equals(qualifiedName)) {
                return springRequestAnnotation;
            }
        }
        return null;
    }

    @Nullable
    public static HttpMethodSpringAnnotation getByShortName(String requestMapping) {
        for (HttpMethodSpringAnnotation springRequestAnnotation : HttpMethodSpringAnnotation.values()) {
            if (springRequestAnnotation.getQualifiedName().endsWith(requestMapping)) {
                return springRequestAnnotation;
            }
        }
        return null;
    }

    @NotNull
    public String getShortName() {
        return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
    }
}