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
 * <p>表示一个具有别名的属性。
 * 当别名属性值为默认值时，优先返回原属性的值，当别名属性不为默认值时，优先返回别名属性的值
 *
 * @author huangchengxing
 * @see AliasLinkAnnotationPostProcessor
 * @see RelationType#ALIAS_FOR
 */
public class AliasedAnnotationAttribute extends AbstractWrappedAnnotationAttribute {

	protected AliasedAnnotationAttribute(AnnotationAttribute origin, AnnotationAttribute linked) {
		super(origin, linked);
	}

	/**
	 * 若{@link #linked}为默认值，则返回{@link #original}的值，否则返回{@link #linked}的值
	 *
	 * @return 属性值
	 */
	@Override
	public Object getValue() {
		return linked.isValueEquivalentToDefaultValue() ? super.getValue() : linked.getValue();
	}

	/**
	 * 当{@link #original}与{@link #linked}都为默认值时返回{@code true}
	 *
	 * @return 是否
	 */
	@Override
	public boolean isValueEquivalentToDefaultValue() {
		return linked.isValueEquivalentToDefaultValue() && original.isValueEquivalentToDefaultValue();
	}
}
