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
package io.github.future0923.debug.tools.base.hutool.core.io.file;

/**
 * 换行符枚举<br>
 * 换行符包括：
 * <pre>
 * Mac系统换行符："\r"
 * Linux系统换行符："\n"
 * Windows系统换行符："\r\n"
 * </pre>
 *
 * @see #MAC
 * @see #LINUX
 * @see #WINDOWS
 * @author Looly
 * @since 3.1.0
 */
public enum LineSeparator {
	/** Mac系统换行符："\r" */
	MAC("\r"),
	/** Linux系统换行符："\n" */
	LINUX("\n"),
	/** Windows系统换行符："\r\n" */
	WINDOWS("\r\n");

	private final String value;

	LineSeparator(String lineSeparator) {
		this.value = lineSeparator;
	}

	public String getValue() {
		return this.value;
	}
}
