package io.github.future0923.debug.tools.extension.spring.request;

import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.util.WebUtils;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author future0923
 */
public class MockAsyncContext implements AsyncContext {

    private final HttpServletRequest request;

    @Nullable
    private final HttpServletResponse response;

    private final List<AsyncListener> listeners = new ArrayList<>();

    @Nullable
    private String dispatchedPath;

    private long timeout = 10 * 1000L;

    private final List<Runnable> dispatchHandlers = new ArrayList<>();


    public MockAsyncContext(ServletRequest request, @Nullable ServletResponse response) {
        this.request = (HttpServletRequest) request;
        this.response = (HttpServletResponse) response;
    }


    public void addDispatchHandler(Runnable handler) {
        Assert.notNull(handler, "Dispatch handler must not be null");
        synchronized (this) {
            if (this.dispatchedPath == null) {
                this.dispatchHandlers.add(handler);
            }
            else {
                handler.run();
            }
        }
    }

    @Override
    public ServletRequest getRequest() {
        return this.request;
    }

    @Override
    @Nullable
    public ServletResponse getResponse() {
        return this.response;
    }

    @Override
    public boolean hasOriginalRequestAndResponse() {
        return (this.request instanceof MockHttpServletRequest && this.response instanceof MockHttpServletResponse);
    }

    @Override
    public void dispatch() {
        dispatch(this.request.getRequestURI());
    }

    @Override
    public void dispatch(String path) {
        dispatch(null, path);
    }

    @Override
    public void dispatch(@Nullable ServletContext context, String path) {
        synchronized (this) {
            this.dispatchedPath = path;
            this.dispatchHandlers.forEach(Runnable::run);
        }
    }

    @Nullable
    public String getDispatchedPath() {
        return this.dispatchedPath;
    }

    @Override
    public void complete() {
        MockHttpServletRequest mockRequest = WebUtils.getNativeRequest(this.request, MockHttpServletRequest.class);
        if (mockRequest != null) {
            mockRequest.setAsyncStarted(false);
        }
        for (AsyncListener listener : this.listeners) {
            try {
                listener.onComplete(new AsyncEvent(this, this.request, this.response));
            }
            catch (IOException ex) {
                throw new IllegalStateException("AsyncListener failure", ex);
            }
        }
    }

    @Override
    public void start(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void addListener(AsyncListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void addListener(AsyncListener listener, ServletRequest request, ServletResponse response) {
        this.listeners.add(listener);
    }

    public List<AsyncListener> getListeners() {
        return this.listeners;
    }

    @Override
    public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
        return BeanUtils.instantiateClass(clazz);
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public long getTimeout() {
        return this.timeout;
    }

}
