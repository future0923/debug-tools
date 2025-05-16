package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.patch;

import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;

/**
 * Dynamic多数据源支持@DS注解热重载
 *
 * @author future0923
 */
public class DynamicPatcher {

    /**
     * 修改DataSourceClassResolver.findKey方法，不从缓存中加载数据源
     */
    @OnClassLoadEvent(classNameRegexp = "com.baomidou.dynamic.datasource.support.DataSourceClassResolver")
    public static void patchDataSourceClassResolver(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod findKey = ctClass.getDeclaredMethod("findKey");
        findKey.setBody("{" +
                "   if ($1.getDeclaringClass() == java.lang.Object.class) {" +
                "       return \"\";" +
                "   }" +
                "   java.lang.String ds = computeDatasource($1, $2);" +
                "   if (ds == null) {" +
                "       return \"\";" +
                "   }" +
                "   return ds;" +
                "}");
    }
}
