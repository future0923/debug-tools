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

import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

/**
 * ANSI标准颜色
 *
 * <p>来自Spring Boot</p>
 *
 * @author Phillip Webb, Geoffrey Chandler
 * @since 5.8.0
 */
public enum AnsiColor implements AnsiElement {

	/**
	 * 默认前景色
	 */
	DEFAULT(39),

	/**
	 * 黑
	 */
	BLACK(30),

	/**
	 * 红
	 */
	RED(31),

	/**
	 * 绿
	 */
	GREEN(32),

	/**
	 * 黄
	 */
	YELLOW(33),

	/**
	 * 蓝
	 */
	BLUE(34),

	/**
	 * 品红
	 */
	MAGENTA(35),

	/**
	 * 青
	 */
	CYAN(36),

	/**
	 * 白
	 */
	WHITE(37),

	/**
	 * 亮黑
	 */
	BRIGHT_BLACK(90),

	/**
	 * 亮红
	 */
	BRIGHT_RED(91),

	/**
	 * 亮绿
	 */
	BRIGHT_GREEN(92),

	/**
	 * 亮黄
	 */
	BRIGHT_YELLOW(93),

	/**
	 * 亮蓝
	 */
	BRIGHT_BLUE(94),

	/**
	 * 亮品红
	 */
	BRIGHT_MAGENTA(95),

	/**
	 * 亮青
	 */
	BRIGHT_CYAN(96),

	/**
	 * 亮白
	 */
	BRIGHT_WHITE(97);

	private final int code;

	AnsiColor(int code) {
		this.code = code;
	}

	/**
	 * 获取ANSI颜色代码
	 *
	 * @return 颜色代码
	 */
	@Override
	public int getCode() {
		return this.code;
	}

	@Override
	public String toString() {
		return StrUtil.toString(this.code);
	}

}
