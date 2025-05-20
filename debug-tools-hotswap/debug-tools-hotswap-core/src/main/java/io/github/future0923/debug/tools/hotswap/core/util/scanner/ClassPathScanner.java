/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.hotswap.core.util.scanner;

import io.github.future0923.debug.tools.base.logging.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 扫描 classpath 目录，每个文件都会调用 ScannerVisitor。
 * <p/>
 * 使用线程上下文类加载器进行扫描。
 */
public class ClassPathScanner implements Scanner {
    private static final Logger LOGGER = Logger.getLogger(ClassPathScanner.class);

    // 扫描时jar中的文件标识 - e.g. jar:file:\J:\DebugTools\target\DebugTools-1.0.jar!\io\github\debug\tools\plugin
    public static final String JAR_URL_SEPARATOR = "!/";
    public static final String JAR_URL_PREFIX = "jar:";
    public static final String ZIP_URL_PREFIX = "zip:";
    public static final String FILE_URL_PREFIX = "file:";


    @Override
    public void scan(ClassLoader classLoader, String path, ScannerVisitor visitor) throws IOException {
        LOGGER.trace("Scanning path {}", path);
        // 查找所有目录 - classpath 目录或 JAR
        Enumeration<URL> en = classLoader == null ? ClassLoader.getSystemResources(path) : classLoader.getResources(path);
        while (en.hasMoreElements()) {
            URL pluginDirURL = en.nextElement();
            File pluginDir = new File(pluginDirURL.getFile());
            if (pluginDir.isDirectory()) {
                scanDirectory(pluginDir, visitor);
            } else {
                // JAR file
                String uri;
                try {
                    uri = pluginDirURL.toURI().toString();
                } catch (URISyntaxException e) {
                    throw new IOException("Illegal directory URI " + pluginDirURL, e);
                }
                if (uri.startsWith(JAR_URL_PREFIX) || uri.startsWith(ZIP_URL_PREFIX)) {
                    String jarFile = uri.substring(uri.indexOf(':') + 1); // remove the prefix
                    scanJar(jarFile, visitor);
                } else {
                    LOGGER.warning("Unknown resource type of file " + uri);
                }
            }
        }
    }

    /**
     * 递归扫描目录
     */
    protected void scanDirectory(File pluginDir, ScannerVisitor visitor) throws IOException {
        LOGGER.trace("Scanning directory " + pluginDir.getName());
        File[] files = pluginDir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, visitor);
            } else if (file.isFile() && file.getName().endsWith(".class")) {
                visitor.visit(new FileInputStream(file));
            }
        }
    }

    /**
     * 扫描jar中的所有entries
     *
     * @param urlFile jar路径
     *                (e.g. jar:file:\J:\DebugTools\target\DebugTools-1.0.jar!\io\github\debug\tools\plugin)
     * @param visitor 访问者
     * @throws IOException exception from a visitor
     */
    private void scanJar(String urlFile, ScannerVisitor visitor) throws IOException {
        LOGGER.trace("Scanning JAR file '{}'", urlFile);

        int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
        JarFile jarFile = null;
        String rootEntryPath;

        try {
            if (separatorIndex != -1) {
                String jarFileUrl = urlFile.substring(0, separatorIndex);
                rootEntryPath = urlFile.substring(separatorIndex + JAR_URL_SEPARATOR.length());
                jarFile = getJarFile(jarFileUrl);
            } else {
                rootEntryPath = "";
                jarFile = new JarFile(urlFile);
            }

            if (!rootEntryPath.isEmpty() && !rootEntryPath.endsWith("/")) {
                rootEntryPath = rootEntryPath + "/";
            }

            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
                JarEntry entry = entries.nextElement();
                String entryPath = entry.getName();

                // class files inside entry
                if (entryPath.startsWith(rootEntryPath) && entryPath.endsWith(".class")) {
                    LOGGER.trace("Visiting JAR entry {}", entryPath);
                    visitor.visit(jarFile.getInputStream(entry));
                }
            }
        } finally {
            if (jarFile != null) {
                jarFile.close();
            }
        }
    }

    /**
     * 将给定的 jar 文件 URL 解析为 JarFile 对象。
     */
    protected JarFile getJarFile(String jarFileUrl) throws IOException {
        LOGGER.trace("Opening JAR file " + jarFileUrl);
        if (jarFileUrl.startsWith(FILE_URL_PREFIX)) {
            try {
                return new JarFile(toURI(jarFileUrl).getSchemeSpecificPart());
            } catch (URISyntaxException ex) {
                // Fallback for URLs that are not valid URIs (should hardly ever happen).
                return new JarFile(jarFileUrl.substring(FILE_URL_PREFIX.length()));
            }
        } else {
            return new JarFile(jarFileUrl);
        }
    }

    /**
     * 将路径转为URI对象
     */
    public static URI toURI(String location) throws URISyntaxException {
        return new URI(location.replace(" ", "%20"));
    }
}
