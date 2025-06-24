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
package io.github.future0923.debug.tools.extension.spring;

import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.extension.spring.request.MockHttpServletRequest;
import io.github.future0923.debug.tools.extension.spring.request.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author future0923
 */
public class SpringServletUtil {

    public static void setRequest(RunDTO runDTO) {
        if (runDTO.getHeaders() != null && !runDTO.getHeaders().isEmpty()) {
            MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
            runDTO.getHeaders().forEach(mockHttpServletRequest::addHeader);
            ServletRequestAttributes requestAttributes = new ServletRequestAttributes(mockHttpServletRequest);
            RequestContextHolder.setRequestAttributes(requestAttributes);
        } else {
            RequestContextHolder.resetRequestAttributes();
        }
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return requestAttributes.getRequest();
        }
        return new MockHttpServletRequest();
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return requestAttributes.getResponse();
        }
        return new MockHttpServletResponse();
    }
}
