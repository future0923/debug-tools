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
package io.github.future0923.debug.tools.base.hutool.core.convert.impl;

import io.github.future0923.debug.tools.base.hutool.core.collection.CollUtil;
import io.github.future0923.debug.tools.base.hutool.core.convert.Converter;
import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.TypeUtil;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * 各种集合类转换器
 *
 * @author Looly
 * @since 3.0.8
 */
public class CollectionConverter implements Converter<Collection<?>> {

	/** 集合类型 */
	private final Type collectionType;
	/** 集合元素类型 */
	private final Type elementType;

	/**
	 * 构造，默认集合类型使用{@link Collection}
	 */
	public CollectionConverter() {
		this(Collection.class);
	}

	// ---------------------------------------------------------------------------------------------- Constractor start
	/**
	 * 构造
	 *
	 * @param collectionType 集合类型
	 */
	public CollectionConverter(Type collectionType) {
		this(collectionType, TypeUtil.getTypeArgument(collectionType));
	}

	/**
	 * 构造
	 *
	 * @param collectionType 集合类型
	 */
	public CollectionConverter(Class<?> collectionType) {
		this(collectionType, TypeUtil.getTypeArgument(collectionType));
	}

	/**
	 * 构造
	 *
	 * @param collectionType 集合类型
	 * @param elementType 集合元素类型
	 */
	public CollectionConverter(Type collectionType, Type elementType) {
		this.collectionType = collectionType;
		this.elementType = elementType;
	}
	// ---------------------------------------------------------------------------------------------- Constractor end

	@Override
	public Collection<?> convert(Object value, Collection<?> defaultValue) throws IllegalArgumentException {
		final Collection<?> result = convertInternal(value);
		return ObjectUtil.defaultIfNull(result, defaultValue);
	}

	/**
	 * 内部转换
	 *
	 * @param value 值
	 * @return 转换后的集合对象
	 */
	protected Collection<?> convertInternal(Object value) {
		final Collection<?> collection = CollUtil.create(TypeUtil.getClass(this.collectionType), TypeUtil.getClass(this.elementType));
		return CollUtil.addAll(collection, value, this.elementType);
	}
}
