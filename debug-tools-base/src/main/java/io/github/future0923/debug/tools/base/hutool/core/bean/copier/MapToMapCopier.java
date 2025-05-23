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
package io.github.future0923.debug.tools.base.hutool.core.bean.copier;

import io.github.future0923.debug.tools.base.hutool.core.util.TypeUtil;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Map属性拷贝到Map中的拷贝器
 *
 * @since 5.8.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class MapToMapCopier extends AbsCopier<Map, Map> {

	/**
	 * 目标的类型（用于泛型类注入）
	 */
	private final Type targetType;

	/**
	 * 构造
	 *
	 * @param source      来源Map
	 * @param target      目标Bean对象
	 * @param targetType  目标泛型类型
	 * @param copyOptions 拷贝选项
	 */
	public MapToMapCopier(Map source, Map target, Type targetType, CopyOptions copyOptions) {
		super(source, target, copyOptions);
		this.targetType = targetType;
	}

	@Override
	public Map copy() {
		this.source.forEach((sKey, sValue) -> {
			if (null == sKey) {
				return;
			}

			if(sKey instanceof String){
				sKey = copyOptions.editFieldName((String) sKey);
				// 对key做转换，转换后为null的跳过
				if (null == sKey) {
					return;
				}
			}

			// 忽略不需要拷贝的 key,
			if (false == copyOptions.testKeyFilter(sKey)) {
				return;
			}

			final Object targetValue = target.get(sKey);
			// 非覆盖模式下，如果目标值存在，则跳过
			if (false == copyOptions.override && null != targetValue) {
				return;
			}

			// 获取目标值真实类型并转换源值
			final Type[] typeArguments = TypeUtil.getTypeArguments(this.targetType);
			if (null != typeArguments) {
				//sValue = Convert.convertWithCheck(typeArguments[1], sValue, null, this.copyOptions.ignoreError);
				sValue = this.copyOptions.convertField(typeArguments[1], sValue);
			}

			// 自定义值
			sValue = copyOptions.editFieldValue(sKey.toString(), sValue);

			// 忽略空值
			if (true == copyOptions.ignoreNullValue && sValue == null) {
				return;
			}

			// 目标赋值
			target.put(sKey, sValue);
		});
		return this.target;
	}
}
