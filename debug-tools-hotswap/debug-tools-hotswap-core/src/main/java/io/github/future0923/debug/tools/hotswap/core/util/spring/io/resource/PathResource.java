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

import io.github.future0923.debug.tools.hotswap.core.util.spring.util.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * {@link Resource} implementation for {@code java.nio.file.Path} handles.
 * <p>
 * Supports resolution as File, and also as URL.
 * <p>
 * Implements the extended {@link WritableResource} interface.
 *
 * @author Philippe Marschall
 * @since 4.0
 * @see Path
 */
public class PathResource extends AbstractResource implements WritableResource {

    private final Path path;

    /**
     * Create a new PathResource from a Path handle.
     * <p>
     * Note: Unlike {@link FileSystemResource}, when building relative resources
     * via {@link #createRelative}, the relative path will be built
     * <i>underneath</i> the given root: e.g. Paths.get("C:/dir1/"), relative
     * path "dir2" -> "C:/dir1/dir2"!
     * 
     * @param path
     *            a Path handle
     */
    public PathResource(Path path) {
        Assert.notNull(path, "Path must not be null");
        this.path = path.normalize();
    }

    /**
     * Create a new PathResource from a Path handle.
     * <p>
     * Note: Unlike {@link FileSystemResource}, when building relative resources
     * via {@link #createRelative}, the relative path will be built
     * <i>underneath</i> the given root: e.g. Paths.get("C:/dir1/"), relative
     * path "dir2" -> "C:/dir1/dir2"!
     * 
     * @param path
     *            a path
     * @see Paths#get(String, String...)
     */
    public PathResource(String path) {
        Assert.notNull(path, "Path must not be null");
        this.path = Paths.get(path).normalize();
    }

    /**
     * Create a new PathResource from a Path handle.
     * <p>
     * Note: Unlike {@link FileSystemResource}, when building relative resources
     * via {@link #createRelative}, the relative path will be built
     * <i>underneath</i> the given root: e.g. Paths.get("C:/dir1/"), relative
     * path "dir2" -> "C:/dir1/dir2"!
     * 
     * @see Paths#get(URI)
     * @param uri
     *            a path URI
     */
    public PathResource(URI uri) {
        Assert.notNull(uri, "URI must not be null");
        this.path = Paths.get(uri).normalize();
    }

    /**
     * Return the file path for this resource.
     */
    public final String getPath() {
        return this.path.toString();
    }

    /**
     * This implementation returns whether the underlying file exists.
     * 
     * @see org.hotswap.agent.util.spring.io.resource.springframework.core.io.PathResource#exists()
     */
    @Override
    public boolean exists() {
        return Files.exists(this.path);
    }

    /**
     * This implementation checks whether the underlying file is marked as
     * readable (and corresponds to an actual file with content, not to a
     * directory).
     * 
     * @see Files#isReadable(Path)
     * @see Files#isDirectory(Path, java.nio.file.LinkOption...)
     */
    @Override
    public boolean isReadable() {
        return (Files.isReadable(this.path) && !Files.isDirectory(this.path));
    }

    /**
     * This implementation opens a InputStream for the underlying file.
     * 
     * @see java.nio.file.spi.FileSystemProvider#newInputStream(Path,
     *      OpenOption...)
     */
    @Override
    public InputStream getInputStream() throws IOException {
        if (!exists()) {
            throw new FileNotFoundException(getPath() + " (no such file or directory)");
        }
        if (Files.isDirectory(this.path)) {
            throw new FileNotFoundException(getPath() + " (is a directory)");
        }
        return Files.newInputStream(this.path);
    }

    /**
     * This implementation returns a URL for the underlying file.
     * 
     * @see Path#toUri()
     * @see URI#toURL()
     */
    @Override
    public URL getURL() throws IOException {
        return this.path.toUri().toURL();
    }

    /**
     * This implementation returns a URI for the underlying file.
     * 
     * @see Path#toUri()
     */
    @Override
    public URI getURI() throws IOException {
        return this.path.toUri();
    }

    /**
     * This implementation returns the underlying File reference.
     */
    @Override
    public File getFile() throws IOException {
        try {
            return this.path.toFile();
        } catch (UnsupportedOperationException ex) {
            // only Paths on the default file system can be converted to a File
            // do exception translation for cases where conversion is not
            // possible
            throw new FileNotFoundException(this.path + " cannot be resolved to " + "absolute file path");
        }
    }

    /**
     * This implementation returns the underlying File's length.
     */
    @Override
    public long contentLength() throws IOException {
        return Files.size(this.path);
    }

    /**
     * This implementation returns the underlying File's timestamp.
     * 
     * @see Files#getLastModifiedTime(Path,
     *      java.nio.file.LinkOption...)
     */
    @Override
    public long lastModified() throws IOException {
        // we can not use the super class method since it uses conversion to a
        // File and
        // only Paths on the default file system can be converted to a File
        return Files.getLastModifiedTime(path).toMillis();
    }

    /**
     * This implementation creates a FileResource, applying the given path
     * relative to the path of the underlying file of this resource descriptor.
     * 
     * @see Path#resolve(String)
     */
    @Override
    public Resource createRelative(String relativePath) throws IOException {
        return new PathResource(this.path.resolve(relativePath));
    }

    /**
     * This implementation returns the name of the file.
     * 
     * @see Path#getFileName()
     */
    @Override
    public String getFilename() {
        return this.path.getFileName().toString();
    }

    @Override
    public String getDescription() {
        return "path [" + this.path.toAbsolutePath() + "]";
    }

    // implementation of WritableResource

    /**
     * This implementation checks whether the underlying file is marked as
     * writable (and corresponds to an actual file with content, not to a
     * directory).
     * 
     * @see Files#isWritable(Path)
     * @see Files#isDirectory(Path, java.nio.file.LinkOption...)
     */
    @Override
    public boolean isWritable() {
        return Files.isWritable(this.path) && !Files.isDirectory(this.path);
    }

    /**
     * This implementation opens a OutputStream for the underlying file.
     * 
     * @see java.nio.file.spi.FileSystemProvider#newOutputStream(Path,
     *      OpenOption...)
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (Files.isDirectory(this.path)) {
            throw new FileNotFoundException(getPath() + " (is a directory)");
        }
        return Files.newOutputStream(this.path);
    }

    /**
     * This implementation compares the underlying Path references.
     */
    @Override
    public boolean equals(Object obj) {
        return (this == obj || (obj instanceof PathResource && this.path.equals(((PathResource) obj).path)));
    }

    /**
     * This implementation returns the hash code of the underlying Path
     * reference.
     */
    @Override
    public int hashCode() {
        return this.path.hashCode();
    }

}