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
package io.github.future0923.debug.tools.extension.spring.jakarta;

import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.MappingMatch;
import org.springframework.lang.Nullable;

/**
 * @author future0923
 */
public class MockHttpServletMapping implements HttpServletMapping {

    private final String matchValue;

    private final String pattern;

    private final String servletName;

    @Nullable
    private final MappingMatch mappingMatch;


    public MockHttpServletMapping(
            String matchValue, String pattern, String servletName, @Nullable MappingMatch match) {

        this.matchValue = matchValue;
        this.pattern = pattern;
        this.servletName = servletName;
        this.mappingMatch = match;
    }


    @Override
    public String getMatchValue() {
        return this.matchValue;
    }

    @Override
    public String getPattern() {
        return this.pattern;
    }

    @Override
    public String getServletName() {
        return this.servletName;
    }

    @Override
    @Nullable
    public MappingMatch getMappingMatch() {
        return this.mappingMatch;
    }


    @Override
    public String toString() {
        return "MockHttpServletMapping [matchValue=\"" + this.matchValue + "\", " +
                "pattern=\"" + this.pattern + "\", servletName=\"" + this.servletName + "\", " +
                "mappingMatch=" + this.mappingMatch + "]";
    }

}

