package io.github.future0923.debug.tools.server.compiler;

import java.net.URI;

/**
 * @author future0923
 */
public class ClassUriWrapper {

    private final URI uri;

    private final String className;

    public ClassUriWrapper(String className, URI uri) {
        this.className = className;
        this.uri = uri;
    }

    public URI getUri() {
        return uri;
    }

    public String getClassName() {
        return className;
    }
}
