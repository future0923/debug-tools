/*
 * Copyright 2013-2024 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
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
