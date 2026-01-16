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