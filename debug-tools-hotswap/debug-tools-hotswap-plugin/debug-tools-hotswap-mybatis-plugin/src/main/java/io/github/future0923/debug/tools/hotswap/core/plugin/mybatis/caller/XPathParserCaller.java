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
