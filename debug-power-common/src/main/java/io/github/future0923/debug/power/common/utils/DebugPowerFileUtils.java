package io.github.future0923.debug.power.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DebugPowerFileUtils {

    private static final String FOLDER_SEPARATOR = "/";

    private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

    private static final String TOP_PATH = "..";

    private static final String CURRENT_PATH = ".";

    /**
     * @return tmp file absolute path
     */
    public static String copyChildFile(File file, String child) throws IOException {
        if (StringUtils.endsWith(file.getPath(), ".jar")) {
            try (JarFile jarFile = new JarFile(file)) {
                // 获取Jar包中所有的文件
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();

                    // 如果该文件是需要提取的资源文件
                    if (DebugPowerFileUtils.pathEquals(entryName, child)) {
                        return getTmpLibFile(jarFile.getInputStream(entry));
                    }
                }
            }
            return "";
        }
        if (file.isDirectory()) {
            file = new File(file.getAbsolutePath(), child);
        }
        return getTmpLibFile(Files.newInputStream(file.toPath()));
    }

    public static String getTmpLibFile(InputStream inputStream) throws IOException {
        return getTmpLibFile(inputStream, "DebugPowerJniLibrary", null);
    }

    public static String getTmpLibFile(InputStream inputStream, String prefix, String suffix) throws IOException {
        File tmpLibFile = File.createTempFile(prefix, suffix);
        try (FileOutputStream tmpLibOutputStream = new FileOutputStream(tmpLibFile);
             InputStream inputStreamNew = inputStream) {
            copy(inputStreamNew, tmpLibOutputStream);
        }
        return tmpLibFile.getAbsolutePath();
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
    }

    public static boolean pathEquals(String path1, String path2) {
        return cleanPath(path1).equals(cleanPath(path2));
    }

    public static String cleanPath(String path) {
        if (StringUtils.isBlank(path)) {
            return path;
        }

        String normalizedPath = StringUtils.replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);
        String pathToUse = normalizedPath;

        // Shortcut if there is no work to do
        if (pathToUse.indexOf('.') == -1) {
            return pathToUse;
        }

        // Strip prefix from path to analyze, to not treat it as part of the
        // first path element. This is necessary to correctly parse paths like
        // "file:core/../core/io/Resource.class", where the ".." should just
        // strip the first "core" directory while keeping the "file:" prefix.
        int prefixIndex = pathToUse.indexOf(':');
        String prefix = "";
        if (prefixIndex != -1) {
            prefix = pathToUse.substring(0, prefixIndex + 1);
            if (prefix.contains(FOLDER_SEPARATOR)) {
                prefix = "";
            } else {
                pathToUse = pathToUse.substring(prefixIndex + 1);
            }
        }
        if (pathToUse.startsWith(FOLDER_SEPARATOR)) {
            prefix = prefix + FOLDER_SEPARATOR;
            pathToUse = pathToUse.substring(1);
        }

        String[] pathArray = StringUtils.split(pathToUse, FOLDER_SEPARATOR);
        // we never require more elements than pathArray and in the common case the same number
        Deque<String> pathElements = new ArrayDeque<>(pathArray.length);
        int tops = 0;

        for (int i = pathArray.length - 1; i >= 0; i--) {
            String element = pathArray[i];
            if (CURRENT_PATH.equals(element)) {
                // Points to current directory - drop it.
            } else if (TOP_PATH.equals(element)) {
                // Registering top path found.
                tops++;
            } else {
                if (tops > 0) {
                    // Merging path element with element corresponding to top path.
                    tops--;
                } else {
                    // Normal path element found.
                    pathElements.addFirst(element);
                }
            }
        }

        // All path elements stayed the same - shortcut
        if (pathArray.length == pathElements.size()) {
            return normalizedPath;
        }
        // Remaining top paths need to be retained.
        for (int i = 0; i < tops; i++) {
            pathElements.addFirst(TOP_PATH);
        }
        // If nothing else left, at least explicitly point to current path.
        if (pathElements.size() == 1 && pathElements.getLast().isEmpty() && !prefix.endsWith(FOLDER_SEPARATOR)) {
            pathElements.addFirst(CURRENT_PATH);
        }

        final String joined = StringUtils.join(FOLDER_SEPARATOR, pathElements);
        // avoid string concatenation with empty prefix
        return prefix.isEmpty() ? joined : prefix + joined;
    }

    public static String getFileAsString(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String temp;
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
            return sb.toString();
        }
    }
}
