package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.caller;

import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.patch.IBatisPatcher;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.apache.ibatis.parsing.XPathParser;

/**
 * {@link XPathParser}的反射调用者
 * @author future0923
 */
public class XPathParserCaller {

    /**
     * {@link IBatisPatcher#patchXMLMapperBuilder(CtClass, ClassPool)}调用获取文件名字
     */
    public static String getSrcFileName(XPathParser parser) {
        return (String) ReflectionHelper.get(parser, IBatisPatcher.SRC_FILE_NAME_FIELD);
    }

}
