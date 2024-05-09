package io.github.future0923.debug.power.core.mock.springmvc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author future0923
 */
public class MockRequestDispatcher implements RequestDispatcher {

    private final Log logger = LogFactory.getLog(getClass());

    private final String resource;


    /**
     * Create a new MockRequestDispatcher for the given resource.
     * @param resource the server resource to dispatch to, located at a
     * particular path or given by a particular name
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
        if (logger.isDebugEnabled()) {
            logger.debug("MockRequestDispatcher: forwarding to [" + this.resource + "]");
        }
    }

    @Override
    public void include(ServletRequest request, ServletResponse response) {
        Assert.notNull(request, "Request must not be null");
        Assert.notNull(response, "Response must not be null");
        getMockHttpServletResponse(response).addIncludedUrl(this.resource);
        if (logger.isDebugEnabled()) {
            logger.debug("MockRequestDispatcher: including [" + this.resource + "]");
        }
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
            return getMockHttpServletResponse(((HttpServletResponseWrapper) response).getResponse());
        }
        throw new IllegalArgumentException("MockRequestDispatcher requires MockHttpServletResponse");
    }

}
