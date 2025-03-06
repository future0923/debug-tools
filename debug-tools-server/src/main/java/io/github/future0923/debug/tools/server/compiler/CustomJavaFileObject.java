package io.github.future0923.debug.tools.server.compiler;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

/**
 * 自定义的JavaFileObject，载入jar文件index时创建对应class的JavaFileObject对象实现动态编译
 *
 * @author future0923
 */
public class CustomJavaFileObject implements JavaFileObject {
    private final String className;
    private final URI uri;

    public CustomJavaFileObject(String className, URI uri) {
        this.uri = uri;
        this.className = className;
    }

    public URI toUri() {
        return uri;
    }

    public InputStream openInputStream() throws IOException {
        return uri.toURL().openStream();
    }

    public OutputStream openOutputStream() {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        return this.className;
    }

    public Reader openReader(boolean ignoreEncodingErrors) {
        throw new UnsupportedOperationException();
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        throw new UnsupportedOperationException();
    }

    public Writer openWriter() throws IOException {
        throw new UnsupportedOperationException();
    }

    public long getLastModified() {
        return 0;
    }

    public boolean delete() {
        throw new UnsupportedOperationException();
    }

    public Kind getKind() {
        return Kind.CLASS;
    }

    public boolean isNameCompatible(String simpleName, Kind kind) {
        return Kind.CLASS.equals(getKind())
                && this.className.endsWith(simpleName);
    }

    public NestingKind getNestingKind() {
        throw new UnsupportedOperationException();
    }

    public Modifier getAccessLevel() {
        throw new UnsupportedOperationException();
    }

    public String getClassName() {
        return this.className;
    }


    public String toString() {
        return this.getClass().getName() + "[" + this.toUri() + "]";
    }
}