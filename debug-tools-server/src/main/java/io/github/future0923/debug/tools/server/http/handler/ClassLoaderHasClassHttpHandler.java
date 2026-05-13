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
package io.github.future0923.debug.tools.server.http.handler;

import com.sun.net.httpserver.Headers;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.common.protocal.http.ClassLoaderHasClassReq;
import io.github.future0923.debug.tools.common.protocal.http.ClassLoaderHasClassRes;

/**
 * 判断目标类是否能被指定 ClassLoader 加载，用于 IDEA 多应用场景自动匹配调用目标。
 *
 * @author future0923
 */
public class ClassLoaderHasClassHttpHandler extends BaseHttpHandler<ClassLoaderHasClassReq, ClassLoaderHasClassRes> {

    public static final ClassLoaderHasClassHttpHandler INSTANCE = new ClassLoaderHasClassHttpHandler();

    public static final String PATH = "/classLoader/hasClass";

    private ClassLoaderHasClassHttpHandler() {

    }

    @Override
    protected ClassLoaderHasClassRes doHandle(ClassLoaderHasClassReq req, Headers responseHeaders) {
        ClassLoaderHasClassRes res = new ClassLoaderHasClassRes();
        if (req == null || StrUtil.isBlank(req.getClassName())) {
            res.setExists(false);
            return res;
        }
        ClassLoader classLoader = findClassLoader(req.getClassLoaderIdentity());
        res.setExists(hasClass(classLoader, req.getClassName()));
        return res;
    }

    static boolean hasClass(ClassLoader classLoader, String className) {
        if (classLoader == null || StrUtil.isBlank(className)) {
            return false;
        }
        try {
            Class.forName(className, false, classLoader);
            return true;
        } catch (Throwable ignore) {
            return false;
        }
    }

    private ClassLoader findClassLoader(String identity) {
        if (StrUtil.isBlank(identity)) {
            return AllClassLoaderHttpHandler.getDefaultClassLoader();
        }
        return AllClassLoaderHttpHandler.getClassLoaderMap().get(identity);
    }
}
