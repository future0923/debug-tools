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
package io.github.future0923.debug.tools.base.hutool.http.body;

import io.github.future0923.debug.tools.base.hutool.core.io.IoUtil;

import java.io.OutputStream;

/**
 * 定义请求体接口
 */
public interface RequestBody {

	/**
	 * 写出数据，不关闭流
	 *
	 * @param out out流
	 */
	void write(OutputStream out);

	/**
	 * 写出并关闭{@link OutputStream}
	 *
	 * @param out {@link OutputStream}
	 * @since 5.7.17
	 */
	default void writeClose(OutputStream out) {
		try {
			write(out);
		} finally {
			IoUtil.close(out);
		}
	}
}
