package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.caller;

import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.register.MybatisXmlRegister;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.apache.ibatis.parsing.XPathParser;

/**
 * @author future0923
 */
public class XPathParserCaller {

    public static String getSrcFileName(XPathParser parser) {
        return (String) ReflectionHelper.get(parser, MybatisXmlRegister.SRC_FILE_NAME_FIELD);
    }

    public static boolean refreshDocument(XPathParser parser) {
        return (boolean) ReflectionHelper.invoke(parser, MybatisXmlRegister.REFRESH_DOCUMENT_METHOD);
    }
}
