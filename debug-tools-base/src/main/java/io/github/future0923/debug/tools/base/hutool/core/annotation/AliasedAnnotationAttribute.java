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
