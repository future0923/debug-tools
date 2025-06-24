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
package io.github.future0923.debug.tools.hotswap.core.util.scanner;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.AnnotationsAttribute;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ClassFile;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.annotation.Annotation;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * ClassPath注解扫描器
 */
public class ClassPathAnnotationScanner {

    private static final Logger LOGGER = Logger.getLogger(ClassPathAnnotationScanner.class);

    // 扫描的注解名（全路径）
    private final String annotation;

    // 扫描器
    private final Scanner scanner;

    public ClassPathAnnotationScanner(String annotation, Scanner scanner) {
        this.annotation = annotation;
        this.scanner = scanner;
    }

    /**
     * 扫描路径，找到含有指定注解的类的ClassName集合
     *
     * @param classLoader 用哪个类加载器加载path
     * @param path        扫描的路径
     * @return 有注解的ClassName集合
     */
    public List<String> scanPlugins(ClassLoader classLoader, String path) throws IOException {
        final List<String> files = new LinkedList<>();
        scanner.scan(classLoader, path, file -> {
            ClassFile cf;
            try {
                DataInputStream stream = new DataInputStream(file);
                cf = new ClassFile(stream);
            } catch (IOException e) {
                throw new IOException("Stream not a valid classFile", e);
            }
            if (hasAnnotation(cf))
                files.add(cf.getName());
        });
        return files;
    }

    /**
     * ClassFile是否含有该注解
     */
    protected boolean hasAnnotation(ClassFile cf) throws IOException {
        AnnotationsAttribute visible = (AnnotationsAttribute) cf.getAttribute(AnnotationsAttribute.visibleTag);
        if (visible != null) {
            for (Annotation ann : visible.getAnnotations()) {
                if (annotation.equals(ann.getTypeName())) {
                    return true;
                }
            }
        }
        return false;
    }


}
