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
package io.github.future0923.debug.tools.hotswap.core.javassist;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * A <code>ByteArrayClassPath</code> contains bytes that is served as
 * a class file to a <code>ClassPool</code>.  It is useful to convert
 * a byte array to a <code>CtClass</code> object.
 *
 * <p>For example, if you want to convert a byte array <code>b</code>
 * into a <code>CtClass</code> object representing the class with a name
 * <code>classname</code>, then do as following:
 *
 * <pre>
 * ClassPool cp = ClassPool.getDefault();
 * cp.insertClassPath(new ByteArrayClassPath(classname, b));
 * CtClass cc = cp.get(classname);
 * </pre>
 *
 * <p>The <code>ClassPool</code> object <code>cp</code> uses the created
 * <code>ByteArrayClassPath</code> object as the source of the class file.
 *
 * <p>A <code>ByteArrayClassPath</code> must be instantiated for every
 * class.  It contains only a single class file.
 *
 * @see javassist.ClassPath
 * @see ClassPool#insertClassPath(ClassPath)
 * @see ClassPool#appendClassPath(ClassPath)
 * @see ClassPool#makeClass(InputStream)
 */
public class ByteArrayClassPath implements ClassPath {
    protected String classname;
    protected byte[] classfile;

    /*
     * Creates a <code>ByteArrayClassPath</code> containing the given
     * bytes.
     *
     * @param name              a fully qualified class name
     * @param classfile         the contents of a class file.
     */
    public ByteArrayClassPath(String name, byte[] classfile) {
        this.classname = name;
        this.classfile = classfile;
    }

    @Override
    public String toString() {
        return "byte[]:" + classname;
    }

    /**
     * Opens the class file.
     */
    @Override
    public InputStream openClassfile(String classname) {
        if(this.classname.equals(classname))
            return new ByteArrayInputStream(classfile);
        return null;
    }

    /**
     * Obtains the URL.
     */
    @Override
    public URL find(String classname) {
        if(this.classname.equals(classname)) {
            String cname = classname.replace('.', '/') + ".class";
            try {
                return new URL(null, "file:/ByteArrayClassPath/" + cname, new BytecodeURLStreamHandler());
            }
            catch (MalformedURLException e) {}
        }

        return null;
    }

    private class BytecodeURLStreamHandler extends URLStreamHandler {
        protected URLConnection openConnection(final URL u) {
            return new BytecodeURLConnection(u);
        }
    }

    private class BytecodeURLConnection extends URLConnection {
        protected BytecodeURLConnection(URL url) {
            super(url);
        }

        public void connect() throws IOException {
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(classfile);
        }

        public int getContentLength() {
            return classfile.length;
        }
    }
}
