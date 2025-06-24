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
package io.github.future0923.debug.tools.hotswap.core.util.spring.io.resource;

import io.github.future0923.debug.tools.hotswap.core.util.spring.util.ResourceUtils;
import io.github.future0923.debug.tools.hotswap.core.util.spring.util.VfsUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

//import org.springframework.util.ResourceUtils;

/**
 * Abstract base class for resources which resolve URLs into File references,
 * such as {@link UrlResource} or {@link ClassPathResource}.
 *
 * <p>
 * Detects the "file" protocol as well as the JBoss "vfs" protocol in URLs,
 * resolving file system references accordingly.
 *
 * @author Juergen Hoeller
 * @since 3.0
 */
public abstract class AbstractFileResolvingResource extends AbstractResource {

    /**
     * This implementation returns a File reference for the underlying class
     * path resource, provided that it refers to a file in the file system.
     * 
     * @see ResourceUtils#getFile(URL,
     *      String)
     */
    @Override
    public File getFile() throws IOException {
        URL url = getURL();
        if (url.getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
            return VfsResourceDelegate.getResource(url).getFile();
        }
        return ResourceUtils.getFile(url, getDescription());
    }

    /**
     * This implementation determines the underlying File (or jar file, in case
     * of a resource in a jar/zip).
     */
    @Override
    protected File getFileForLastModifiedCheck() throws IOException {
        URL url = getURL();
        if (ResourceUtils.isJarURL(url)) {
            URL actualUrl = ResourceUtils.extractJarFileURL(url);
            if (actualUrl.getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
                return VfsResourceDelegate.getResource(actualUrl).getFile();
            }
            return ResourceUtils.getFile(actualUrl, "Jar URL");
        } else {
            return getFile();
        }
    }

    /**
     * This implementation returns a File reference for the underlying class
     * path resource, provided that it refers to a file in the file system.
     * 
     * @see ResourceUtils#getFile(URI, String)
     */
    protected File getFile(URI uri) throws IOException {
        if (uri.getScheme().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
            return VfsResourceDelegate.getResource(uri).getFile();
        }
        return ResourceUtils.getFile(uri, getDescription());
    }

    @Override
    public boolean exists() {
        try {
            URL url = getURL();
            if (ResourceUtils.isFileURL(url)) {
                // Proceed with file system resolution...
                return getFile().exists();
            } else {
                // Try a URL connection content-length header...
                URLConnection con = url.openConnection();
                customizeConnection(con);
                HttpURLConnection httpCon = (con instanceof HttpURLConnection ? (HttpURLConnection) con : null);
                if (httpCon != null) {
                    int code = httpCon.getResponseCode();
                    if (code == HttpURLConnection.HTTP_OK) {
                        return true;
                    } else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
                        return false;
                    }
                }
                if (con.getContentLength() >= 0) {
                    return true;
                }
                if (httpCon != null) {
                    // no HTTP OK status, and no content-length header: give up
                    httpCon.disconnect();
                    return false;
                } else {
                    // Fall back to stream existence: can we open the stream?
                    try (InputStream is = getInputStream()) {
                        return true;
                    }
                }
            }
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public boolean isReadable() {
        try {
            URL url = getURL();
            if (ResourceUtils.isFileURL(url)) {
                // Proceed with file system resolution...
                File file = getFile();
                return (file.canRead() && !file.isDirectory());
            } else {
                return true;
            }
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public long contentLength() throws IOException {
        URL url = getURL();
        if (ResourceUtils.isFileURL(url)) {
            // Proceed with file system resolution...
            return getFile().length();
        } else {
            // Try a URL connection content-length header...
            URLConnection con = url.openConnection();
            customizeConnection(con);
            return con.getContentLength();
        }
    }

    @Override
    public long lastModified() throws IOException {
        URL url = getURL();
        if (ResourceUtils.isFileURL(url) || ResourceUtils.isJarURL(url)) {
            // Proceed with file system resolution...
            return super.lastModified();
        } else {
            // Try a URL connection last-modified header...
            URLConnection con = url.openConnection();
            customizeConnection(con);
            return con.getLastModified();
        }
    }

    /**
     * Customize the given {@link URLConnection}, obtained in the course of an
     * {@link #exists()}, {@link #contentLength()} or {@link #lastModified()}
     * call.
     * <p>
     * Calls {@link ResourceUtils#useCachesIfNecessary(URLConnection)} and
     * delegates to {@link #customizeConnection(HttpURLConnection)} if possible.
     * Can be overridden in subclasses.
     * 
     * @param con
     *            the URLConnection to customize
     * @throws IOException
     *             if thrown from URLConnection methods
     */
    protected void customizeConnection(URLConnection con) throws IOException {
        ResourceUtils.useCachesIfNecessary(con);
        if (con instanceof HttpURLConnection) {
            customizeConnection((HttpURLConnection) con);
        }
    }

    /**
     * Customize the given {@link HttpURLConnection}, obtained in the course of
     * an {@link #exists()}, {@link #contentLength()} or {@link #lastModified()}
     * call.
     * <p>
     * Sets request method "HEAD" by default. Can be overridden in subclasses.
     * 
     * @param con
     *            the HttpURLConnection to customize
     * @throws IOException
     *             if thrown from HttpURLConnection methods
     */
    protected void customizeConnection(HttpURLConnection con) throws IOException {
        con.setRequestMethod("HEAD");
    }

    /**
     * Inner delegate class, avoiding a hard JBoss VFS API dependency at
     * runtime.
     */
    private static class VfsResourceDelegate {

        public static Resource getResource(URL url) throws IOException {
            return new VfsResource(VfsUtils.getRoot(url));
        }

        public static Resource getResource(URI uri) throws IOException {
            return new VfsResource(VfsUtils.getRoot(uri));
        }
    }

}