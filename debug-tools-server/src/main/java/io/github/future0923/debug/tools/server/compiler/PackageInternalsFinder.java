package io.github.future0923.debug.tools.server.compiler;


import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.stream.Collectors;

/**
 * 在指定的 {@link #classLoader} 下搜索指定的 packageName 下的所有类文件，返回对应的 {@link CustomJavaFileObject} 集合，用来支持java代码的动态编译
 *
 * @author future0923
 */
public class PackageInternalsFinder {

    /**
     * 要搜索的类加载器
     */
    private final ClassLoader classLoader;

    /**
     * 扩展名
     */
    private static final String CLASS_FILE_EXTENSION = ".class";

    /**
     * 缓存
     * key jar的uri地址
     * value jar中的文件索引信息
     */
    private static final Map<String, JarFileIndex> INDEX_MAPPING = new ConcurrentHashMap<>();

    /**
     * 初始化
     *
     * @param classLoader 指定对应ClassLoader
     */
    public PackageInternalsFinder(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 在 ClassLoader 的资源路径中查找指定 packageName 下的 .class 文件。
     * 1. 将 Java 包名转换为文件路径格式（com.example → com/example）。
     * 2. 获取所有资源 URL（可能是目录或 JAR）。
     * 3. 调用 {@link #listUnder(String, URL)} 方法，获取该路径下的所有 .class 文件。
     *
     * @param packageName 指定包名
     * @return class文件对应的CustomJavaFileObject集合
     */
    public List<JavaFileObject> find(String packageName) throws IOException {
        String javaPackageName = packageName.replaceAll("\\.", "/");
        List<JavaFileObject> result = new ArrayList<>();
        Enumeration<URL> urlEnumeration = classLoader.getResources(javaPackageName);
        while (urlEnumeration.hasMoreElements()) { // one URL for each jar on the classpath that has the given package
            URL packageFolderURL = urlEnumeration.nextElement();
            result.addAll(listUnder(packageName, packageFolderURL));
        }
        return result;
    }

    private Collection<JavaFileObject> listUnder(String packageName, URL packageFolderURL) {
        File directory = new File(decode(packageFolderURL.getFile()));
        if (directory.isDirectory()) { // browse local .class files - useful for local execution
            return processDir(packageName, directory);
        } else { // browse a jar file
            return processJar(packageName, packageFolderURL);
        }
    }

    /**
     * 处理jar
     */
    private List<JavaFileObject> processJar(String packageName, URL packageFolderURL) {
        try {
            String jarUri = packageFolderURL.toExternalForm().substring(0, packageFolderURL.toExternalForm().lastIndexOf("!/"));
            JarFileIndex jarFileIndex = INDEX_MAPPING.get(jarUri);
            if (jarFileIndex == null) {
                jarFileIndex = new JarFileIndex(jarUri, URI.create(jarUri + "!/"));
                INDEX_MAPPING.put(jarUri, jarFileIndex);
            }
            List<JavaFileObject> result = jarFileIndex.search(packageName);
            if (result != null) {
                return result;
            }
        } catch (Exception e) {
            // ignore
        }
        // 保底
        return fuse(packageFolderURL);
    }

    /**
     * 加载所有
     */
    private List<JavaFileObject> fuse(URL packageFolderURL) {
        List<JavaFileObject> result = new ArrayList<JavaFileObject>();
        try {
            String jarUri = packageFolderURL.toExternalForm().substring(0, packageFolderURL.toExternalForm().lastIndexOf("!/"));
            JarURLConnection jarConn = (JarURLConnection) packageFolderURL.openConnection();
            String rootEntryName = jarConn.getEntryName();
            if (rootEntryName != null) {
                //可能为 null（自己没有类文件时）
                int rootEnd = rootEntryName.length() + 1;
                Enumeration<JarEntry> entryEnum = jarConn.getJarFile().entries();
                while (entryEnum.hasMoreElements()) {
                    JarEntry jarEntry = entryEnum.nextElement();
                    String name = jarEntry.getName();
                    if (name.startsWith(rootEntryName) && name.indexOf('/', rootEnd) == -1 && name.endsWith(CLASS_FILE_EXTENSION)) {
                        URI uri = URI.create(jarUri + "!/" + name);
                        String binaryName = name.replaceAll("/", ".");
                        binaryName = binaryName.replaceAll(CLASS_FILE_EXTENSION + "$", "");
                        result.add(new CustomJavaFileObject(binaryName, uri));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Wasn't able to open " + packageFolderURL + " as a jar file", e);
        }
        return result;
    }

    /**
     * 处理目录
     */
    private List<JavaFileObject> processDir(String packageName, File directory) {
        File[] files = directory.listFiles(item ->
                item.isFile() && getKind(item.getName()) == JavaFileObject.Kind.CLASS);
        if (files != null) {
            return Arrays.stream(files).map(item -> {
                String className = packageName + "." + item.getName()
                        .replaceAll(CLASS_FILE_EXTENSION + "$", "");
                return new CustomJavaFileObject(className, item.toURI());
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private String decode(String filePath) {
        try {
            return URLDecoder.decode(filePath, "utf-8");
        } catch (Exception ignored) {
        }
        return filePath;
    }

    /**
     * 通过文件名转为kind
     */
    public static JavaFileObject.Kind getKind(String name) {
        if (name.endsWith(JavaFileObject.Kind.CLASS.extension)) {
            return JavaFileObject.Kind.CLASS;
        } else if (name.endsWith(JavaFileObject.Kind.SOURCE.extension)) {
            return JavaFileObject.Kind.SOURCE;
        } else if (name.endsWith(JavaFileObject.Kind.HTML.extension)) {
            return JavaFileObject.Kind.HTML;
        } else {
            return JavaFileObject.Kind.OTHER;
        }
    }

    /**
     * jar 文件索引
     */
    public static class JarFileIndex {

        /**
         * jar 文件的 URI, eg: jar:file:/Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/jre/lib/deploy.jar
         */
        private final String jarUri;

        /**
         * jar 文件的 URI, eg: jar:file:/Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/jre/lib/deploy.jar!/
         */
        private final URI uri;

        /**
         * 类名和类地址文件映射
         */
        private final Map<String, List<ClassUriWrapper>> packages = new HashMap<>();

        public JarFileIndex(String jarUri, URI uri) throws IOException {
            this.jarUri = jarUri;
            this.uri = uri;
            loadIndex();
        }

        /**
         * 载入jar索引
         */
        private void loadIndex() throws IOException {
            JarURLConnection jarConn = (JarURLConnection) uri.toURL().openConnection();
            String rootEntryName = jarConn.getEntryName() == null ? "" : jarConn.getEntryName();
            Enumeration<JarEntry> entryEnum = jarConn.getJarFile().entries();
            while (entryEnum.hasMoreElements()) {
                JarEntry jarEntry = entryEnum.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.startsWith(rootEntryName) && entryName.endsWith(CLASS_FILE_EXTENSION)) {
                    String className = entryName
                            .substring(0, entryName.length() - CLASS_FILE_EXTENSION.length())
                            .replace(rootEntryName, "")
                            .replace("/", ".");
                    if (className.startsWith(".")) {
                        className = className.substring(1);
                    }
                    if (className.equals("package-info")
                            || className.equals("module-info")
                            || className.lastIndexOf(".") == -1) {
                        continue;
                    }
                    String packageName = className.substring(0, className.lastIndexOf("."));
                    List<ClassUriWrapper> classes = packages.computeIfAbsent(packageName, k -> new ArrayList<>());
                    classes.add(new ClassUriWrapper(className, URI.create(jarUri + "!/" + entryName)));
                }
            }
        }

        /**
         * 通过 packages 信息搜索返回对应的 CustomJavaFileObject
         */
        public List<JavaFileObject> search(String packageName) {
            if (this.packages.isEmpty()) {
                return null;
            }
            if (this.packages.containsKey(packageName)) {
                return packages.get(packageName).stream().map(item -> new CustomJavaFileObject(item.getClassName(), item.getUri())).collect(Collectors.toList());
            }
            return Collections.emptyList();
        }
    }
}
