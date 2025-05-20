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
package io.github.future0923.debug.tools.base.hutool.core.util;

import io.github.future0923.debug.tools.base.hutool.core.convert.Convert;
import io.github.future0923.debug.tools.base.hutool.core.exceptions.UtilException;
import io.github.future0923.debug.tools.base.hutool.core.map.MapUtil;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.util.Map;

/**
 * JNDI工具类<br>
 * JNDI是Java Naming and Directory Interface（JAVA命名和目录接口）的英文简写，<br>
 * 它是为JAVA应用程序提供命名和目录访问服务的API（Application Programing Interface，应用程序编程接口）。
 *
 * <p>
 * 见：https://blog.csdn.net/u010430304/article/details/54601302
 * </p>
 *
 * @author loolY
 * @since 5.7.7
 */
public class JNDIUtil {

	/**
	 * 创建{@link InitialDirContext}
	 *
	 * @param environment 环境参数，{@code null}表示无参数
	 * @return {@link InitialDirContext}
	 */
	public static InitialDirContext createInitialDirContext(Map<String, String> environment) {
		try {
			if (MapUtil.isEmpty(environment)) {
				return new InitialDirContext();
			}
			return new InitialDirContext(Convert.convert(Hashtable.class, environment));
		} catch (NamingException e) {
			throw new UtilException(e);
		}
	}

	/**
	 * 创建{@link InitialContext}
	 *
	 * @param environment 环境参数，{@code null}表示无参数
	 * @return {@link InitialContext}
	 */
	public static InitialContext createInitialContext(Map<String, String> environment) {
		try {
			if (MapUtil.isEmpty(environment)) {
				return new InitialContext();
			}
			return new InitialContext(Convert.convert(Hashtable.class, environment));
		} catch (NamingException e) {
			throw new UtilException(e);
		}
	}

	/**
	 * 获取指定容器环境的对象的属性<br>
	 * 如获取DNS属性，则URI为类似：dns:hutool.cn
	 *
	 * @param uri     URI字符串，格式为[scheme:][name]/[domain]
	 * @param attrIds 需要获取的属性ID名称
	 * @return {@link Attributes}
	 */
	public static Attributes getAttributes(String uri, String... attrIds) {
		try {
			return createInitialDirContext(null).getAttributes(uri, attrIds);
		} catch (NamingException e) {
			throw new UtilException(e);
		}
	}
}
