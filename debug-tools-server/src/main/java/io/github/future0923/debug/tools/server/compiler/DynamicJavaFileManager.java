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


import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 动态编译的文件管理器，用于动态编译Java代码
 *
 * @author future0923
 */
public class DynamicJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private static final String[] superLocationNames = {
            javax.tools.StandardLocation.PLATFORM_CLASS_PATH.name(),
            // JPMS StandardLocation.SYSTEM_MODULES
            "SYSTEM_MODULES"
    };

    private final PackageInternalsFinder finder;

    private final DynamicClassLoader classLoader;

    private final List<MemoryByteCode> byteCodes = new ArrayList<MemoryByteCode>();

    public DynamicJavaFileManager(JavaFileManager fileManager, DynamicClassLoader classLoader) {
        super(fileManager);
        this.classLoader = classLoader;
        this.finder = new PackageInternalsFinder(classLoader);
    }

    /**
     * 重写这个方法可以指定字节码存储位置。
     * 让 .class 文件存储在内存中不是磁盘中，结合JavaCompiler可以在不写入磁盘的情况下编译java代码
     */
    @Override
    public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className,
                                               JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        for (MemoryByteCode byteCode : byteCodes) {
            if (byteCode.getClassName().equals(className)) {
                return byteCode;
            }
        }
        MemoryByteCode innerClass = new MemoryByteCode(className);
        byteCodes.add(innerClass);
        classLoader.registerCompiledSource(innerClass);
        return innerClass;
    }

    // 导致lombok无法编译的bug
    //@Override
    //public ClassLoader getClassLoader(JavaFileManager.Location location) {
    //    return classLoader;
    //}

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (file instanceof CustomJavaFileObject) {
            return ((CustomJavaFileObject) file).getClassName();
        } else {
            return super.inferBinaryName(location, file);
        }
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        if (location instanceof StandardLocation) {
            String locationName = ((StandardLocation) location).name();
            for (String name : superLocationNames) {
                if (name.equals(locationName)) {
                    return super.list(location, packageName, kinds, recurse);
                }
            }
        }
        // 从指定的类加载器合并 JavaFileObjects
        if (location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) {
            return new IterableJoin<>(
                    super.list(location, packageName, kinds, recurse),
                    finder.find(packageName)
            );
        }

        return super.list(location, packageName, kinds, recurse);
    }

    static class IterableJoin<T> implements Iterable<T> {
        private final Iterable<T> first, next;

        public IterableJoin(Iterable<T> first, Iterable<T> next) {
            this.first = first;
            this.next = next;
        }

        @Override
        public Iterator<T> iterator() {
            return new IteratorJoin<T>(first.iterator(), next.iterator());
        }
    }

    static class IteratorJoin<T> implements Iterator<T> {
        private final Iterator<T> first, next;

        public IteratorJoin(Iterator<T> first, Iterator<T> next) {
            this.first = first;
            this.next = next;
        }

        @Override
        public boolean hasNext() {
            return first.hasNext() || next.hasNext();
        }

        @Override
        public T next() {
            if (first.hasNext()) {
                return first.next();
            }
            return next.next();
        }

    }
}
