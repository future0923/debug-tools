package io.github.future0923.debug.tools.idea.search.annotations;

public enum SpringControllerAnnotation implements PathMappingAnnotation {
    /**
     * org.springframework.stereotype.Controller
     */
    CONTROLLER("Controller", "org.springframework.stereotype.Controller"),
    /**
     * org.springframework.cloud.openfeign.FeignClient
     */
    FEIGN_CLIENT("FeignClient", "org.springframework.cloud.openfeign.FeignClient"),
    /**
     * org.springframework.web.bind.annotation.RestController
     */
    REST_CONTROLLER("RestController", "org.springframework.web.bind.annotation.RestController");

    private final String shortName;
    private final String qualifiedName;

    SpringControllerAnnotation(String shortName, String qualifiedName) {
        this.shortName = shortName;
        this.qualifiedName = qualifiedName;
    }

    @Override
    public String getQualifiedName() {
        return qualifiedName;
    }

    @Override
    public String getShortName() {
        return shortName;
    }

}