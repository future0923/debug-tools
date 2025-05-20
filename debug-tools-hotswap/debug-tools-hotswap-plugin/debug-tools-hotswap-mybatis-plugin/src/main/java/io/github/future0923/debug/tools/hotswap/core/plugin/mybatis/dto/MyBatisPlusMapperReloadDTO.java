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
package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.dto;

/**
 * @author future0923
 */
public class MyBatisPlusMapperReloadDTO {

    private final ClassLoader appClassLoader;

    private final Class<?> clazz;

    private final byte[] bytes;

    private final String path;

    public MyBatisPlusMapperReloadDTO(ClassLoader appClassLoader, Class<?> clazz, byte[] bytes, String path) {
        this.appClassLoader = appClassLoader;
        this.clazz = clazz;
        this.bytes = bytes;
        this.path = path;
    }

    public ClassLoader getAppClassLoader() {
        return appClassLoader;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getPath() {
        return path;
    }
}
