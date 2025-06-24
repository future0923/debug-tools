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

import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;
import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * {@link AnnotationAttribute}的基本实现
 *
 * @author huangchengxing
 */
public class CacheableAnnotationAttribute implements AnnotationAttribute {

	private volatile boolean valueInvoked;
	private Object value;

	private boolean defaultValueInvoked;
	private Object defaultValue;

	private final Annotation annotation;
	private final Method attribute;

	public CacheableAnnotationAttribute(Annotation annotation, Method attribute) {
		Assert.notNull(annotation, "annotation must not null");
		Assert.notNull(attribute, "attribute must not null");
		this.annotation = annotation;
		this.attribute = attribute;
		this.valueInvoked = false;
		this.defaultValueInvoked = false;
	}

	@Override
	public Annotation getAnnotation() {
		return this.annotation;
	}

	@Override
	public Method getAttribute() {
		return this.attribute;
	}

	@Override
	public Object getValue() {
		if (!valueInvoked) {
			synchronized (this) {
				if (!valueInvoked) {
					valueInvoked = true;
					value = ReflectUtil.invoke(annotation, attribute);
				}
			}
		}
		return value;
	}

	@Override
	public boolean isValueEquivalentToDefaultValue() {
		if (!defaultValueInvoked) {
			defaultValue = attribute.getDefaultValue();
			defaultValueInvoked = true;
		}
		return ObjectUtil.equals(getValue(), defaultValue);
	}

}
