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
package io.github.future0923.debug.tools.hotswap.core.plugin.gson;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;

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
