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
package io.github.future0923.debug.tools.base.hutool.core.convert;

import java.lang.reflect.Type;

/**
 * 类型转换接口函数，根据给定的值和目标类型，由用户自定义转换规则。
 *
 * @author looly
 * @since 5.8.0
 */
@FunctionalInterface
public interface TypeConverter {

	/**
	 * 转换为指定类型<br>
	 * 如果类型无法确定，将读取默认值的类型做为目标类型
	 *
	 * @param targetType 目标Type，非泛型类使用
	 * @param value      原始值
	 * @return 转换后的值
	 * @throws IllegalArgumentException 无法确定目标类型，且默认值为{@code null}，无法确定类型
	 */
	Object convert(Type targetType, Object value);
}
