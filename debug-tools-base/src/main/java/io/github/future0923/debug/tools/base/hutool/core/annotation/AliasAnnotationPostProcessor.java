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
import io.github.future0923.debug.tools.base.hutool.core.lang.Opt;
import io.github.future0923.debug.tools.base.hutool.core.map.ForestMap;
import io.github.future0923.debug.tools.base.hutool.core.map.LinkedForestMap;
import io.github.future0923.debug.tools.base.hutool.core.map.TreeEntry;
import io.github.future0923.debug.tools.base.hutool.core.util.ClassUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;

import java.util.Map;

/**
 * <p>用于处理注解对象中带有{@link Alias}注解的属性。<br>
 * 当该处理器执行完毕后，{@link Alias}注解指向的目标注解的属性将会被包装并替换为
 * {@link ForceAliasedAnnotationAttribute}。
 *
 * @author huangchengxing
 * @see Alias
 * @see ForceAliasedAnnotationAttribute
 */
public class AliasAnnotationPostProcessor implements SynthesizedAnnotationPostProcessor {

	@Override
	public int order() {
		return Integer.MIN_VALUE;
	}

	@Override
	public void process(SynthesizedAnnotation synthesizedAnnotation, AnnotationSynthesizer synthesizer) {
		final Map<String, AnnotationAttribute> attributeMap = synthesizedAnnotation.getAttributes();

		// 记录别名与属性的关系
		final ForestMap<String, AnnotationAttribute> attributeAliasMappings = new LinkedForestMap<>(false);
		attributeMap.forEach((attributeName, attribute) -> {
			final String alias = Opt.ofNullable(attribute.getAnnotation(Alias.class))
				.map(Alias::value)
				.orElse(null);
			if (ObjectUtil.isNull(alias)) {
				return;
			}
			final AnnotationAttribute aliasAttribute = attributeMap.get(alias);
			Assert.notNull(aliasAttribute, "no method for alias: [{}]", alias);
			attributeAliasMappings.putLinkedNodes(alias, aliasAttribute, attributeName, attribute);
		});

		// 处理别名
		attributeMap.forEach((attributeName, attribute) -> {
			final AnnotationAttribute resolvedAttribute = Opt.ofNullable(attributeName)
				.map(attributeAliasMappings::getRootNode)
				.map(TreeEntry::getValue)
				.orElse(attribute);
			Assert.isTrue(
				ObjectUtil.isNull(resolvedAttribute)
					|| ClassUtil.isAssignable(attribute.getAttributeType(), resolvedAttribute.getAttributeType()),
				"return type of the root alias method [{}] is inconsistent with the original [{}]",
				resolvedAttribute.getClass(), attribute.getAttributeType()
			);
			if (attribute != resolvedAttribute) {
				attributeMap.put(attributeName, new ForceAliasedAnnotationAttribute(attribute, resolvedAttribute));
			}
		});
		synthesizedAnnotation.setAttributes(attributeMap);
	}

}
