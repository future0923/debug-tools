package io.github.future0923.debug.tools.hotswap.core.plugin.spring.patch;

import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * <a href="https://github.com/future0923/debug-tools/issues/210">issue210</a>
 *
 * @author future0923
 */
public class RequestParamPatcher {

    @OnClassLoadEvent(classNameRegexp = "org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver")
    public static void patchAbstractNamedValueMethodArgumentResolver(CtClass ctClass, ClassPool classPool) throws CannotCompileException, NotFoundException {
        CtMethod getNamedValueInfo = ctClass.getDeclaredMethod("getNamedValueInfo", new CtClass[]{classPool.get("org.springframework.core.MethodParameter")});
        getNamedValueInfo.insertBefore("{" +
                "   this.namedValueInfoCache.remove($1);" +
                "}");
    }

    @OnClassLoadEvent(classNameRegexp = "org.springframework.core.LocalVariableTableParameterNameDiscoverer")
    public static void patchLocalVariableTableParameterNameDiscoverer(CtClass ctClass, ClassPool classPool) throws CannotCompileException, NotFoundException {
        CtMethod getParameterNames = ctClass.getDeclaredMethod("doGetParameterNames", new CtClass[]{classPool.get("java.lang.reflect.Executable")});
        getParameterNames.setBody("{" +
                "   Class declaringClass = $1.getDeclaringClass();" +
                "   java.util.Map map = this.inspectClass(declaringClass);" +
                "   if (map != NO_DEBUG_INFO_MAP) {" +
                "       return (String[])map.get($1);" +
                "   }" +
                "   return null;" +
                "}");
    }
}
