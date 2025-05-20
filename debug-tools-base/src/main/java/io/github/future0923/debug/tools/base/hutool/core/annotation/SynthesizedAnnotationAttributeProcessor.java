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
