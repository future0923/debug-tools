package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.hscglib;

import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.AbstractProxyBytecodeTransformer;

/**
 * 转换新的 Cglib 代理定义的字节码，使其在首次访问其中一个方法时初始化。
 *
 * @author future0923
 */
public class CglibProxyBytecodeTransformer extends AbstractProxyBytecodeTransformer {

    public CglibProxyBytecodeTransformer(ClassPool classPool) {
        super(classPool);
    }

    @Override
    protected String getInitCall(CtClass cc, String initFieldName) throws Exception {
        CtMethod[] methods = cc.getDeclaredMethods();
        StringBuilder strB = new StringBuilder();
        for (CtMethod ctMethod : methods) {
            if (ctMethod.getName().startsWith("CGLIB$STATICHOOK")) {
                ctMethod.insertAfter(initFieldName + "=true;");
                strB.insert(0, ctMethod.getName() + "();");
                break;
            }
        }

        if (strB.length() == 0) {
            throw new RuntimeException("Could not find CGLIB$STATICHOOK method");
        }
        return strB.toString() + "CGLIB$BIND_CALLBACKS(this);";
    }
}
