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
package io.github.future0923.debug.tools.base.hutool.core.codec;

/**
 * 解码接口
 *
 * @param <T> 被解码的数据类型
 * @param <R> 解码后的数据类型
 * @author looly
 * @since 5.7.22
 */
public interface Decoder<T, R> {

	/**
	 * 执行解码
	 *
	 * @param encoded 被解码的数据
	 * @return 解码后的数据
	 */
	R decode(T encoded);
}
