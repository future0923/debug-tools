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
package io.github.future0923.debug.tools.base.hutool.core.io;

import io.github.future0923.debug.tools.base.hutool.core.collection.CollUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.HashSet;
import java.util.Set;

/**
 * 带有类验证的对象流，用于避免反序列化漏洞<br>
 * 详细见：https://xz.aliyun.com/t/41/
 *
 * @author looly
 * @since 5.2.6
 */
public class ValidateObjectInputStream extends ObjectInputStream {

	private Set<String> whiteClassSet;
	private Set<String> blackClassSet;

	/**
	 * 构造
	 *
	 * @param inputStream 流
	 * @param acceptClasses 白名单的类
	 * @throws IOException IO异常
	 */
	public ValidateObjectInputStream(InputStream inputStream, Class<?>... acceptClasses) throws IOException {
		super(inputStream);
		accept(acceptClasses);
	}

	/**
	 * 禁止反序列化的类，用于反序列化验证
	 *
	 * @param refuseClasses 禁止反序列化的类
	 * @since 5.3.5
	 */
	public void refuse(Class<?>... refuseClasses) {
		if(null == this.blackClassSet){
			this.blackClassSet = new HashSet<>();
		}
		for (Class<?> acceptClass : refuseClasses) {
			this.blackClassSet.add(acceptClass.getName());
		}
	}

	/**
	 * 接受反序列化的类，用于反序列化验证
	 *
	 * @param acceptClasses 接受反序列化的类
	 */
	public void accept(Class<?>... acceptClasses) {
		if(null == this.whiteClassSet){
			this.whiteClassSet = new HashSet<>();
		}
		for (Class<?> acceptClass : acceptClasses) {
			this.whiteClassSet.add(acceptClass.getName());
		}
	}

	/**
	 * 只允许反序列化SerialObject class
	 */
	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		validateClassName(desc.getName());
		return super.resolveClass(desc);
	}

	/**
	 * 验证反序列化的类是否合法
	 * @param className 类名
	 * @throws InvalidClassException 非法类
	 */
	private void validateClassName(String className) throws InvalidClassException {
		// 黑名单
		if(CollUtil.isNotEmpty(this.blackClassSet)){
			if(this.blackClassSet.contains(className)){
				throw new InvalidClassException("Unauthorized deserialization attempt by black list", className);
			}
		}

		if(CollUtil.isEmpty(this.whiteClassSet)){
			return;
		}
		if(className.startsWith("java.")){
			// java中的类默认在白名单中
			return;
		}
		if(this.whiteClassSet.contains(className)){
			return;
		}

		throw new InvalidClassException("Unauthorized deserialization attempt", className);
	}
}
