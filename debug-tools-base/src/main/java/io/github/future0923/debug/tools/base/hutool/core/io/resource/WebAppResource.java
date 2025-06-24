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
package io.github.future0923.debug.tools.base.hutool.core.io.resource;

import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;

import java.io.File;

/**
 * Web root资源访问对象
 *
 * @author looly
 * @since 4.1.11
 */
public class WebAppResource extends FileResource {
	private static final long serialVersionUID = 1L;

	/**
	 * 构造
	 *
	 * @param path 相对于Web root的路径
	 */
	public WebAppResource(String path) {
		super(new File(FileUtil.getWebRoot(), path));
	}

}
