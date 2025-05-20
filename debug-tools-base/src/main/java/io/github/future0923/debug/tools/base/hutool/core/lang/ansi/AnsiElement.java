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
package io.github.future0923.debug.tools.base.hutool.core.lang.ansi;

/**
 * ANSI可转义节点接口，实现为ANSI颜色等
 *
 * <p>来自Spring Boot</p>
 *
 * @author Phillip Webb
 */
public interface AnsiElement {

	/**
	 * @return ANSI转义编码
	 */
	@Override
	String toString();

	/**
	 * 获取ANSI代码，默认返回-1
	 * @return ANSI代码
	 * @since 5.8.7
	 */
	default int getCode(){
		return -1;
	}
}
