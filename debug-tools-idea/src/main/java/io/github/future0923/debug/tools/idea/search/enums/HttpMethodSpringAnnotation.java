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