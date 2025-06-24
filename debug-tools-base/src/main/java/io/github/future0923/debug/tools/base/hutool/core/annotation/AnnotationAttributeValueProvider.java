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
package io.github.future0923.debug.tools.base.hutool.core.annotation;

/**
 * 表示一个可以从当前接口的实现类中，获得特定的属性值
 */
@FunctionalInterface
public interface AnnotationAttributeValueProvider {

	/**
	 * 获取注解属性值
	 *
	 * @param attributeName  属性名称
	 * @param attributeType  属性类型
	 * @return 注解属性值
	 */
	Object getAttributeValue(String attributeName, Class<?> attributeType);

}
