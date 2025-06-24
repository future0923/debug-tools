/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.base.hutool.core.util;

import io.github.future0923.debug.tools.base.hutool.core.exceptions.UtilException;
import io.github.future0923.debug.tools.base.hutool.core.io.FastByteArrayOutputStream;
import io.github.future0923.debug.tools.base.hutool.core.io.IORuntimeException;
import io.github.future0923.debug.tools.base.hutool.core.io.IoUtil;
import io.github.future0923.debug.tools.base.hutool.core.io.ValidateObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * 序列化工具类<br>
 * 注意！此工具类依赖于JDK的序列化机制，某些版本的JDK中可能存在远程注入漏洞。
 *
 * @author looly
 * @since 5.6.3
 */
public class SerializeUtil {

	/**
	 * 序列化后拷贝流的方式克隆<br>
	 * 对象必须实现Serializable接口
	 *
	 * @param <T> 对象类型
	 * @param obj 被克隆对象
	 * @return 克隆后的对象
	 * @throws UtilException IO异常和ClassNotFoundException封装
	 */
	public static <T> T clone(T obj) {
		if (false == (obj instanceof Serializable)) {
			return null;
		}
		return deserialize(serialize(obj));
	}

	/**
	 * 序列化<br>
	 * 对象必须实现Serializable接口
	 *
	 * @param <T> 对象类型
	 * @param obj 要被序列化的对象
	 * @return 序列化后的字节码
	 */
	public static <T> byte[] serialize(T obj) {
		if (false == (obj instanceof Serializable)) {
			return null;
		}
		final FastByteArrayOutputStream byteOut = new FastByteArrayOutputStream();
		IoUtil.writeObjects(byteOut, false, (Serializable) obj);
		return byteOut.toByteArray();
	}

	/**
	 * 反序列化<br>
	 * 对象必须实现Serializable接口
	 *
	 * <p>
	 * 注意！！！ 此方法不会检查反序列化安全，可能存在反序列化漏洞风险！！！
	 * </p>
	 *
	 * @param <T>   对象类型
	 * @param bytes 反序列化的字节码
	 * @param acceptClasses 白名单的类
	 * @return 反序列化后的对象
	 */
	public static <T> T deserialize(byte[] bytes, Class<?>... acceptClasses) {
		try {
			return IoUtil.readObj(new ValidateObjectInputStream(
					new ByteArrayInputStream(bytes), acceptClasses));
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}
}
