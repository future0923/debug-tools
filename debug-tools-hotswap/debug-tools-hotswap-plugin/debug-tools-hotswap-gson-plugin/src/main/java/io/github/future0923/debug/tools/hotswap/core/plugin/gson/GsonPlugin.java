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
package io.github.future0923.debug.tools.hotswap.core.plugin.gson;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * @author future0923
 */
@Plugin(
        name = "Gson",
        description = "Reload Gson cache after class definition/change.",
        testedVersions = {"All between 2.9.1"}
)
public class GsonPlugin {

    private static final Logger logger = Logger.getLogger(GsonPlugin.class);

    @OnClassLoadEvent(classNameRegexp = "com.google.gson.Gson")
    public static void patchGson(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod getAdapter = ctClass.getDeclaredMethod("getAdapter", new CtClass[]{classPool.get("com.google.gson.reflect.TypeToken")});
        getAdapter.insertBefore("{" +
                "   typeTokenCache.remove($1);" +
                "}");
        logger.info("patch gson success");
    }
}
