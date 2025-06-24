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
package io.github.future0923.debug.tools.base.hutool.core.lang.ansi;

import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

/**
 * ANSI文本样式风格枚举
 *
 * <p>来自Spring Boot</p>
 *
 * @author Phillip Webb
 * @since 5.8.0
 */
public enum AnsiStyle implements AnsiElement {

	/**
	 * 重置/正常
	 */
	NORMAL(0),

	/**
	 * 粗体或增加强度
	 */
	BOLD(1),

	/**
	 * 弱化（降低强度）
	 */
	FAINT(2),

	/**
	 * 斜体
	 */
	ITALIC(3),

	/**
	 * 下划线
	 */
	UNDERLINE(4);

	private final int code;

	AnsiStyle(int code) {
		this.code = code;
	}

	/**
	 * 获取ANSI文本样式风格代码
	 *
	 * @return 文本样式风格代码
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
