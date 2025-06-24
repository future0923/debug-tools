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
package io.github.future0923.debug.tools.base.hutool.core.bean.copier;

import java.lang.reflect.Type;

/**
 * JSON自定义转换扩展接口,因core模块无法直接调用json模块而创建,
 * 使用此接口避免使用反射调用toBean方法而性能太差。
 *
 * @author mkeq
 * @since 5.8.22
 */
public interface IJSONTypeConverter {

	/**
	 * 转为实体类对象
	 *
	 * @param <T>  Bean类型
	 * @param type {@link Type}
	 * @return 实体类对象
	 * @since 3.0.8
	 */
	<T> T toBean(Type type);

}
