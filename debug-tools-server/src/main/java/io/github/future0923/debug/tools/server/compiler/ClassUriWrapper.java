package io.github.future0923.debug.tools.server.compiler;

import java.net.URI;

/**
 * 类地址信息包装器
 *
 * @author future0923
 */
public class ClassUriWrapper {

    /**
     * 类文件的地址
     */
    private final URI uri;

    /**
     * 类名
     */
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
