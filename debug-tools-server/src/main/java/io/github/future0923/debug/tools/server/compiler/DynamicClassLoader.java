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

import java.util.HashMap;
import java.util.Map;

/**
 * 动态编译类加载器
 *
 * @author future0923
 */
public class DynamicClassLoader extends ClassLoader {

    private final Map<String, MemoryByteCode> byteCodes = new HashMap<>();

    public DynamicClassLoader(ClassLoader classLoader) {
        super(classLoader);
    }

    /**
     * 注册对应的编译资源文件
     */
    public void registerCompiledSource(MemoryByteCode byteCode) {
        byteCodes.put(byteCode.getClassName(), byteCode);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        MemoryByteCode byteCode = byteCodes.get(name);
        if (byteCode == null) {
            return super.findClass(name);
        }
        return super.defineClass(name, byteCode.getByteCode(), 0, byteCode.getByteCode().length);
    }

    /**
     * 获取编译信息的class对象
     */
    public Map<String, Class<?>> getClasses() throws ClassNotFoundException {
        Map<String, Class<?>> classes = new HashMap<>();
        for (MemoryByteCode byteCode : byteCodes.values()) {
            classes.put(byteCode.getClassName(), findClass(byteCode.getClassName()));
        }
        return classes;
    }

    /**
     * 获取编译信息的class字节码
     */
    public Map<String, byte[]> getByteCodes() {
        Map<String, byte[]> result = new HashMap<>(byteCodes.size());
        for (Map.Entry<String, MemoryByteCode> entry : byteCodes.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getByteCode());
        }
        return result;
    }
}