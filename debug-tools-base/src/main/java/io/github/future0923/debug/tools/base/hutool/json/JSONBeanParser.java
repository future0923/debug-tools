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
package io.github.future0923.debug.tools.base.hutool.json;

/**
 * 实现此接口的类可以通过实现{@code parse(value)}方法来将JSON中的值解析为此对象的值
 *
 * @author Looly
 * @since 5.7.8
 * @param <T> 参数类型
 */
public interface JSONBeanParser<T> {

	/**
	 * value转Bean<br>
	 * 通过实现此接口，将JSON中的值填充到当前对象的字段值中，即对象自行实现JSON反序列化逻辑
	 *
	 * @param value 被解析的对象类型，可能为JSON或者普通String、Number等
	 */
	void parse(T value);
}
