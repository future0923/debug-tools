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

import io.github.future0923.debug.tools.base.hutool.core.bean.BeanUtil;
import io.github.future0923.debug.tools.base.hutool.core.bean.PropDesc;
import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;
import io.github.future0923.debug.tools.base.hutool.core.util.TypeUtil;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Bean属性拷贝到Map中的拷贝器
 *
 * @since 5.8.0
 */
@SuppressWarnings("rawtypes")
public class BeanToMapCopier extends AbsCopier<Object, Map> {

	/**
	 * 目标的Map类型（用于泛型类注入）
	 */
	private final Type targetType;

	/**
	 * 构造
	 *
	 * @param source      来源Map
	 * @param target      目标Map对象
	 * @param targetType  目标泛型类型
	 * @param copyOptions 拷贝选项
	 */
	public BeanToMapCopier(Object source, Map target, Type targetType, CopyOptions copyOptions) {
		super(source, target, copyOptions);
		this.targetType = targetType;
	}

	@Override
	public Map copy() {
		Class<?> actualEditable = source.getClass();
		if (null != copyOptions.editable) {
			// 检查限制类是否为target的父类或接口
			Assert.isTrue(copyOptions.editable.isInstance(source),
					"Source class [{}] not assignable to Editable class [{}]", actualEditable.getName(), copyOptions.editable.getName());
			actualEditable = copyOptions.editable;
		}

		final Map<String, PropDesc> sourcePropDescMap = BeanUtil.getBeanDesc(actualEditable).getPropMap(copyOptions.ignoreCase);
		sourcePropDescMap.forEach((sFieldName, sDesc) -> {
			if (null == sFieldName || false == sDesc.isReadable(copyOptions.transientSupport)) {
				// 字段空或不可读，跳过
				return;
			}

			sFieldName = copyOptions.editFieldName(sFieldName);
			// 对key做转换，转换后为null的跳过
			if (null == sFieldName) {
				return;
			}

			// 忽略不需要拷贝的 key,
			if (false == copyOptions.testKeyFilter(sFieldName)) {
				return;
			}

			// 检查源对象属性是否过滤属性
			Object sValue = sDesc.getValue(this.source);
			if (false == copyOptions.testPropertyFilter(sDesc.getField(), sValue)) {
				return;
			}

			// 获取目标值真实类型并转换源值
			final Type[] typeArguments = TypeUtil.getTypeArguments(this.targetType);
			if(null != typeArguments && typeArguments.length > 1){
				//sValue = Convert.convertWithCheck(typeArguments[1], sValue, null, this.copyOptions.ignoreError);
				sValue = this.copyOptions.convertField(typeArguments[1], sValue);
			}

			// 自定义值
			sValue = copyOptions.editFieldValue(sFieldName, sValue);

			// 目标赋值
			if(null != sValue || false == copyOptions.ignoreNullValue){
				//noinspection unchecked
				target.put(sFieldName, sValue);
			}
		});
		return this.target;
	}
}
