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

import java.io.IOException;
import java.io.InputStream;

/**
 * 扫描目录中的文件，每个文件都会调用{@link ScannerVisitor#visit(InputStream)}
 */
public interface Scanner {

    /**
     * 扫描路径中的所有文件并调用{@link ScannerVisitor#visit(InputStream)}
     *
     * @param classLoader 用哪个类加载器加载path
     * @param path        用'/'分隔的资源路径
     *                    Semantics same as {@link ClassLoader#getResources}.
     * @param visitor     扫描器访问者
     */
    void scan(ClassLoader classLoader, String path, ScannerVisitor visitor) throws IOException;
}
