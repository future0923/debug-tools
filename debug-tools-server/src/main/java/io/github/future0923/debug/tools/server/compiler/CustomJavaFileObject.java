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