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

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.util.Assert;

/**
 * @author future0923
 */
public class MockRequestDispatcher implements RequestDispatcher {

    private final String resource;


    /**
     * Create a new MockRequestDispatcher for the given resource.
     *
     * @param resource the server resource to dispatch to, located at a
     *                 particular path or given by a particular name
     */
    public MockRequestDispatcher(String resource) {
        Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
    }


    @Override
    public void forward(ServletRequest request, ServletResponse response) {
        Assert.notNull(request, "Request must not be null");
        Assert.notNull(response, "Response must not be null");
        Assert.state(!response.isCommitted(), "Cannot perform forward - response is already committed");
        getMockHttpServletResponse(response).setForwardedUrl(this.resource);
    }

    @Override
    public void include(ServletRequest request, ServletResponse response) {
        Assert.notNull(request, "Request must not be null");
        Assert.notNull(response, "Response must not be null");
        getMockHttpServletResponse(response).addIncludedUrl(this.resource);
    }

    /**
     * Obtain the underlying {@link MockHttpServletResponse}, unwrapping
     * {@link HttpServletResponseWrapper} decorators if necessary.
     */
    protected MockHttpServletResponse getMockHttpServletResponse(ServletResponse response) {
        if (response instanceof MockHttpServletResponse) {
            return (MockHttpServletResponse) response;
        }
        if (response instanceof HttpServletResponseWrapper) {
            HttpServletResponseWrapper wrapper = (HttpServletResponseWrapper) response;
            return getMockHttpServletResponse(wrapper.getResponse());
        }
        throw new IllegalArgumentException("MockRequestDispatcher requires MockHttpServletResponse");
    }

}
