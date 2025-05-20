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
