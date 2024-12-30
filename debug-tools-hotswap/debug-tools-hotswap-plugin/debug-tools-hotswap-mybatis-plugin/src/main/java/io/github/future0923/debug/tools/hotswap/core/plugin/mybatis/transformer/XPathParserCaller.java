/*
 * Copyright 2013-2019 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.transformer;

import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.apache.ibatis.parsing.XPathParser;

/**
 * The Class XPathParserCaller - workaround to call synthetic XPathParser methods since XPathParser is patched after XMLConfigBuilder
 *
 * @author Vladimir Dvorak
 */
public class XPathParserCaller {

    public static String getSrcFileName(XPathParser parser) {
        return (String) ReflectionHelper.get(parser, IbatisTransformer.SRC_FILE_NAME_FIELD);
    }

    public static boolean refreshDocument(XPathParser parser) {
        return (boolean) ReflectionHelper.invoke(parser, IbatisTransformer.REFRESH_DOCUMENT_METHOD);
    }
}
