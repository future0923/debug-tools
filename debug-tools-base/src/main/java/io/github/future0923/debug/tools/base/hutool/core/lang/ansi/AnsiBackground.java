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
 * ANSI背景颜色枚举
 *
 * <p>来自Spring Boot</p>
 *
 * @author Phillip Webb, Geoffrey Chandler
 * @since 5.8.0
 */
public enum AnsiBackground implements AnsiElement {

	/**
	 * 默认背景色
	 */
	DEFAULT(49),

	/**
	 * 黑色
	 */
	BLACK(40),

	/**
	 * 红
	 */
	RED(41),

	/**
	 * 绿
	 */
	GREEN(42),

	/**
	 * 黄
	 */
	YELLOW(43),

	/**
	 * 蓝
	 */
	BLUE(44),

	/**
	 * 品红
	 */
	MAGENTA(45),

	/**
	 * 青
	 */
	CYAN(46),

	/**
	 * 白
	 */
	WHITE(47),

	/**
	 * 亮黑
	 */
	BRIGHT_BLACK(100),

	/**
	 * 亮红
	 */
	BRIGHT_RED(101),

	/**
	 * 亮绿
	 */
	BRIGHT_GREEN(102),

	/**
	 * 亮黄
	 */
	BRIGHT_YELLOW(103),

	/**
	 * 亮蓝
	 */
	BRIGHT_BLUE(104),

	/**
	 * 亮品红
	 */
	BRIGHT_MAGENTA(105),

	/**
	 * 亮青
	 */
	BRIGHT_CYAN(106),

	/**
	 * 亮白
	 */
	BRIGHT_WHITE(107);

	private final int code;

	AnsiBackground(int code) {
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
