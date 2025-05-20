package io.github.future0923.debug.tools.hotswap.core.plugin.gson;

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
        name = "HuTool",
        description = "Reload HuTool cache after class definition/change.",
        testedVersions = {"All between 2.9.1"}
)
public class GsonPlugin {

    @OnClassLoadEvent(classNameRegexp = "com.google.gson.Gson")
    public static void patchGson(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod getAdapter = ctClass.getDeclaredMethod("getAdapter", new CtClass[]{classPool.get("com.google.gson.reflect.TypeToken")});
        getAdapter.insertBefore("{" +
                "   typeTokenCache.remove($1);" +
                "}");
    }
}
