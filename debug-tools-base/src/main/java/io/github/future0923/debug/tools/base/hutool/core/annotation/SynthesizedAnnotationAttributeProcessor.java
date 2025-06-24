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


import java.util.Collection;

/**
 * 合成注解属性选择器。用于在{@link SynthesizedAggregateAnnotation}中从指定类型的合成注解里获取到对应的属性值
 *
 * @author huangchengxing
 */
@FunctionalInterface
public interface SynthesizedAnnotationAttributeProcessor {

	/**
	 * 从一批被合成注解中，获取指定名称与类型的属性值
	 *
	 * @param attributeName          属性名称
	 * @param attributeType          属性类型
	 * @param synthesizedAnnotations 被合成的注解
	 * @param <R> 属性类型
	 * @return 属性值
	 */
	<R> R getAttributeValue(String attributeName, Class<R> attributeType, Collection<? extends SynthesizedAnnotation> synthesizedAnnotations);

}
