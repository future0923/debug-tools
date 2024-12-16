package io.github.future0923.debug.tools.idea.api.enums;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Spring方法注解
 */
@Getter
public enum SpringHttpMethodAnnotation {

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

    SpringHttpMethodAnnotation(String qualifiedName, HttpMethod method) {
        this.qualifiedName = qualifiedName;
        this.method = method;
    }

    @Nullable
    public static SpringHttpMethodAnnotation getByQualifiedName(String qualifiedName) {
        for (SpringHttpMethodAnnotation springRequestAnnotation : SpringHttpMethodAnnotation.values()) {
            if (springRequestAnnotation.getQualifiedName().equals(qualifiedName)) {
                return springRequestAnnotation;
            }
        }
        return null;
    }

    @Nullable
    public static SpringHttpMethodAnnotation getByShortName(String requestMapping) {
        for (SpringHttpMethodAnnotation springRequestAnnotation : SpringHttpMethodAnnotation.values()) {
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